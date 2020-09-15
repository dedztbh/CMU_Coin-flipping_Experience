package operator

import org.nd4j.linalg.api.ndarray.INDArray
import kotlin.math.pow

/**
 * Created by DEDZTBH on 2020/09/15.
 * Project CMU_Coin-flipping_Experience
 */
class Prob0Init(N: Int) : ProbFinder(N) {
    fun eval(probs: INDArray): INDArray {
        operations.forEach {
            it.apply {
                when (this) {
                    is Matrix -> {
                        probs.muli(opVec)
                        probs.addi(opBias)
                    }
                    is CNot -> {
                        val x = probs.getDouble(i)
                        val y = probs.getDouble(j)
                        probs.putScalar(j.toLong(), (1.0 - x) * y + x * (1.0 - y))
                    }
                    is CSwap -> {
                        val x = probs.getDouble(i)
                        val y = probs.getDouble(j)
                        val z = probs.getDouble(k)
                        probs.putScalar(j.toLong(), (1.0 - x) * y + x * z)
                        probs.putScalar(k.toLong(), (1.0 - x) * z + x * y)
                    }
                    is CCNot -> {
                        val x = probs.getDouble(i)
                        val y = probs.getDouble(j)
                        val z = probs.getDouble(k)
                        val probBoth0 = (1.0 - x) * (1.0 - y)
                        probs.putScalar(k.toLong(), probBoth0 * z + (1.0 - probBoth0) * (1.0 - z))
                    }
                    is Gen1Bit -> {
                        val x = probs.getDouble(i)
                        probs.putScalar(i.toLong(), x * (1.0 - q) * x + (1.0 - x) * (p + (1.0 - p) * x))
                    }
                }
            }
        }
        return probs
    }

    override fun printResult() {
        val probs = eval(getZeroVec())
        repeat(2.0.pow(N).toInt()) {
            val endState = IntArray(N) { i -> (it shr i) and 1 }.apply { reverse() }
            var prob = 1.0
            endState.forEach { i ->
                prob *= if (i > 0) probs.getDouble(i) else (1.0 - probs.getDouble(i))
            }
            println("Pr[%s] = %.9f".format(endState.joinToString(","), prob))
        }
    }
}