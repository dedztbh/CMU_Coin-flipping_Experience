package operator

import org.ejml.data.FMatrixRMaj
import util.readFloat
import util.readInt

sealed class Operation
data class MatrixOp(val opVec: FMatrixRMaj, val opBias: FMatrixRMaj) : Operation()
data class CNot(val i: Int, val j: Int) : Operation()
data class CSwap(val i: Int, val j: Int, val k: Int) : Operation()
data class CCNot(val i: Int, val j: Int, val k: Int) : Operation()
data class Gen1Bit(val i: Int, val p: Float, val q: Float) : Operation()

/**
 * Created by DEDZTBH on 2020/09/15.
 * Project CMU_Coin-flipping_Experience
 */
abstract class ProbFinder(val N: Int) : Operator {
    fun getOneVec(): FMatrixRMaj = FMatrixRMaj(arrayOf(FloatArray(N) { 1f }))
    fun getZeroVec(): FMatrixRMaj = FMatrixRMaj(1, N)
    fun saveMatrix() {
        if (matrixDirty) {
            operations.add(MatrixOp(opVec, opBias))
            opVec = getOneVec()
            opBias = getZeroVec()
            matrixDirty = false
        }
    }

    var opVec = getOneVec()
    var opBias = getZeroVec()
    var matrixDirty = false

    val operations = mutableListOf<Operation>()

    override fun runCmd(cmd: String): Int {
        val i = readInt()
        when (cmd) {
            "Flip" -> {
                opVec[i] = 0f
                opBias[i] = 0.5f
                matrixDirty = true
            }
            "Not" -> {
                opVec[i] *= -1f
                opBias[i] = 1f - opBias[i]
                matrixDirty = true
            }
            "CNot" -> { // cannot represent in matrix op
                val j = readInt()
                saveMatrix()
                operations.add(CNot(i, j))
            }
            "CSwap" -> { // cannot represent in matrix op
                val j = readInt()
                val k = readInt()
                saveMatrix()
                operations.add(CSwap(i, j, k))
            }
            "CCNOT" -> { // cannot represent in matrix op
                val j = readInt()
                val k = readInt()
                saveMatrix()
                operations.add(CCNot(i, j, k))
            }
            "GenFlip" -> {
                val j = readFloat()
                opVec[i] = 0f
                opBias[i] = j
                matrixDirty = true
            }
            "Gen1Bit" -> { // cannot represent in matrix op
                val p = readFloat()
                val q = readFloat()
                saveMatrix()
                operations.add(Gen1Bit(i, p, q))
            }
            else -> return -1
        }
        return 0
    }

    override fun done() = saveMatrix()
}