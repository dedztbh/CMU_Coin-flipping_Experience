package util

import org.ejml.data.FMatrixRMaj
import org.ejml.dense.row.CommonOps_FDRM

/**
 * Created by DEDZTBH on 2020/09/18.
 * Project CMU_Coin-flipping_Experience
 */

infix fun FMatrixRMaj.getColumn(i: Int) = CommonOps_FDRM.extractColumn(this, i, null)

fun FMatrixRMaj.putColumn(i: Int, col: FMatrixRMaj) = col.data.forEachIndexed { rowi, fl -> set(rowi, i, fl) }

infix fun FMatrixRMaj.elemMult(other: FMatrixRMaj) = run {
    val c = createLike()
    CommonOps_FDRM.elementMult(this, other, c)
    c
}

infix fun FMatrixRMaj.scale(d: Float) = run {
    val res = createLike()
    CommonOps_FDRM.scale(d, this, res)
    res
}