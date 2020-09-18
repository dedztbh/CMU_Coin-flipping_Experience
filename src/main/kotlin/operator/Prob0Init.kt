package operator

import org.ejml.data.FMatrixRMaj
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.kotlin.plusAssign
import kotlin.math.pow

/**
 * Created by DEDZTBH on 2020/09/15.
 * Project CMU_Coin-flipping_Experience
 */

fun allStates(n: Int) =
    Array(2.0.pow(n).toInt()) {
        IntArray(n) { i -> (it shr i) and 1 }.apply { reverse() }
    }

class Prob0Init(N: Int) : ProbFinder(N) {
    fun eval(probs: FMatrixRMaj): FMatrixRMaj {
        operations.forEach {
            it.apply {
                when (this) {
                    is MatrixOp -> {
                        CommonOps_FDRM.elementMult(probs, opVec)
                        probs += opBias
                    }
                    is CNot -> {
                        val x = probs[i]
                        val y = probs[j]
                        probs[j] = (1f - x) * y + x * (1f - y)
                    }
                    is CSwap -> {
                        val x = probs[i]
                        val y = probs[j]
                        val z = probs[k]
                        val xFlip = 1f - x
                        probs[j] = xFlip * y + x * z
                        probs[k] = xFlip * z + x * y
                    }
                    is CCNot -> {
                        val x = probs[i]
                        val y = probs[j]
                        val z = probs[k]
                        val probBoth0 = (1f - x) * (1f - y)
                        probs[k] = probBoth0 * z + (1f - probBoth0) * (1f - z)
                    }
                    is Gen1Bit -> {
                        val x = probs[i]
                        probs[i] = x * (1f - q) * x + (1f - x) * (p + (1f - p) * x)
                    }
                }
            }
        }
        return probs
    }

    override fun printResult() {
        val probs = eval(getZeroVec())
        allStates(N).forEach { endState ->
            var prob = 1f
            endState.forEach { i ->
                val x = probs[i]
                prob *= if (i > 0) x else 1f - x
            }
            println("Pr[%s] = %.10f".format(endState.joinToString(","), prob))
        }
    }
}