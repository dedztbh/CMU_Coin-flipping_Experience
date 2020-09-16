package operator

import allStates
import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

/**
 * Created by DEDZTBH on 2020/09/15.
 * Project CMU_Coin-flipping_Experience
 */
class ProbAllInit(N: Int) : ProbFinder(N) {
    fun eval(probs: INDArray): INDArray {
        operations.forEach {
            it.apply {
                when (this) {
                    is Matrix -> {
                        probs.muliRowVector(opVec)
                        probs.addiRowVector(opBias)
                    }
                    is CNot -> {
                        val i = i.toLong()
                        val j = j.toLong()
                        val x = probs.getColumn(i)
                        val y = probs.getColumn(j)
                        //(1.0 - x) * y + x * (1.0 - y)
                        val newCol = x.mul(y).mul(-2.0).add(x).add(y)
                        probs.putColumn(this.j, newCol)
                    }
                    is CSwap -> {
                        val i = i.toLong()
                        val j = j.toLong()
                        val k = k.toLong()
                        val x = probs.getColumn(i)
                        val y = probs.getColumn(j)
                        val z = probs.getColumn(k)
                        val xy = x.mul(y)
                        val xz = x.mul(z)
                        //(1.0 - x) * y + x * z
                        probs.putColumn(this.j, y.sub(xy).add(xz))
                        //(1.0 - x) * z + x * y
                        probs.putColumn(this.k, z.sub(xz).add(xy))
                    }
                    is CCNot -> {
                        val i = i.toLong()
                        val j = j.toLong()
                        val k = k.toLong()
                        val x = probs.getColumn(i)
                        val y = probs.getColumn(j)
                        val z = probs.getColumn(k)
                        //(1.0 - x) * (1.0 - y)
                        val probBoth0 = x.mul(y).sub(x).sub(y).add(1.0)
                        //probBoth0 * z + (1.0 - probBoth0) * (1.0 - z)
                        probs.putColumn(this.k, probBoth0.mul(z).mul(2.0).sub(probBoth0).sub(z).add(1.0))
                    }
                    is Gen1Bit -> {
                        val i = i.toLong()
                        val x = probs.getColumn(i)
                        val xx = x.mul(x)
                        val p_comp = 1.0 - p
                        probs.putColumn(
                            this.i,
                            //x * (1.0 - q) * x + (1.0 - x) * (p + (1.0 - p) * x)
                            //= xx(1-q) + p + (1-p)x - xp - xx(1-p)
                            xx.mul(1.0 - q).add(p).add(x.mul(p_comp)).sub(x.mul(p)).sub(xx.mul(p_comp))
                        )
                    }
                }
            }
        }
        return probs
    }

    override fun printResult() {
        val probs = eval(
            // a 2^n by n matrix
            Nd4j.create(allStates(N)).castTo(DataType.DOUBLE)
        )
        println(probs)
    }
}