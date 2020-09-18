package operator

import org.ejml.data.FMatrixRMaj
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.kotlin.minus
import org.ejml.kotlin.plus
import org.ejml.kotlin.plusAssign
import org.ejml.kotlin.times
import util.elemMult
import util.getColumn
import util.putColumn
import util.scale
import kotlin.math.pow

/**
 * Created by DEDZTBH on 2020/09/15.
 * Project CMU_Coin-flipping_Experience
 */

fun allStatesFloat(n: Int) =
    Array(2.0.pow(n).toInt()) {
        FloatArray(n) { i -> ((it shr i) and 1).toFloat() }.apply { reverse() }
    }

class ProbAllInit(N: Int) : ProbFinder(N) {
    fun eval(probs: FMatrixRMaj): FMatrixRMaj {
        val broadcastVec =
            FMatrixRMaj(probs.numRows,
                1,
                true,
                *FloatArray(probs.numRows) { 1f })
        operations.forEach {
            it.apply {
                when (this) {
                    is MatrixOp -> {
                        CommonOps_FDRM.elementMult(probs, broadcastVec * opVec)
                        probs += broadcastVec * opBias
                    }
                    is CNot -> {
                        val x = probs getColumn i
                        val y = probs getColumn j
                        //(1.0 - x) * y + x * (1.0 - y)
                        probs.putColumn(j, (x elemMult y scale -2f) + x + y)
                    }
                    is CSwap -> {
                        val x = probs getColumn i
                        val y = probs getColumn j
                        val z = probs getColumn k
                        val xy = x elemMult y
                        val xz = x elemMult z
                        //(1.0 - x) * y + x * z
                        probs.putColumn(j, y - xy + xz)
                        //(1.0 - x) * z + x * y
                        probs.putColumn(k, z - xz + xy)
                    }
                    is CCNot -> {
                        val x = probs getColumn i
                        val y = probs getColumn j
                        val z = probs getColumn k
                        //(1.0 - x) * (1.0 - y)
                        val probBoth0 = (x elemMult y) - x - y + 1f
                        //probBoth0 * z + (1.0 - probBoth0) * (1.0 - z)
                        probs.putColumn(k, (probBoth0 elemMult z scale 2f) - probBoth0 - z + 1f)
                    }
                    is Gen1Bit -> {
                        val x = probs getColumn i
                        val xx = x elemMult x
                        val pComp = 1f - p
                        //x * (1.0 - q) * x + (1.0 - x) * (p + (1.0 - p) * x)
                        //= xx(1-q) + p + (1-p)x - xp - xx(1-p)
                        probs.putColumn(
                            i, (xx scale 1f - q) + p + (x scale pComp) - (x scale p) - (xx scale pComp)
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
            FMatrixRMaj(allStatesFloat(N))
        )
        probs.print()
    }
}