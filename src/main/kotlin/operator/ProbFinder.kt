package operator

import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import readDouble
import readInt

sealed class Operation
data class Matrix(val opVec: INDArray, val opBias: INDArray): Operation()
data class CNot(val i: Int, val j: Int): Operation()
data class CSwap(val i: Int, val j: Int, val k: Int): Operation()
data class CCNot(val i: Int, val j: Int, val k: Int): Operation()
data class Gen1Bit(val i: Int, val p: Double, val q: Double): Operation()

/**
 * Created by DEDZTBH on 2020/09/15.
 * Project CMU_Coin-flipping_Experience
 */
abstract class ProbFinder(val N: Int) : Operator {
    fun getOneVec(): INDArray = Nd4j.ones(1, N).castTo(DataType.DOUBLE)
    fun getZeroVec(): INDArray = Nd4j.zeros(1, N).castTo(DataType.DOUBLE)
    fun saveMatrix() {
        if (matrixDirty) {
            operations.add(Matrix(opVec, opBias))
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
        val il = i.toLong()
        when (cmd) {
            "Flip" -> {
                opVec.putScalar(il, 0.0)
                opBias.putScalar(il, 0.5)
                matrixDirty = true
            }
            "Not" -> {
                opVec.putScalar(il, -opVec.getDouble(il))
                opBias.putScalar(il, 1.0 - opBias.getDouble(il))
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
                val j = readDouble()
                opVec.putScalar(il, 0.0)
                opBias.putScalar(il, j)
                matrixDirty = true
            }
            "Gen1Bit" -> { // cannot represent in matrix op
                val p = readDouble()
                val q = readDouble()
                saveMatrix()
                operations.add(Gen1Bit(i, p, q))
            }
            else -> return -1
        }
        return 0
    }

    override fun done() = saveMatrix()
}