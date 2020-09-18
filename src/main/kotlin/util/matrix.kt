package util

import org.ejml.data.FMatrixRMaj
import org.ejml.dense.row.CommonOps_FDRM

/**
 * Created by DEDZTBH on 2020/09/18.
 * Project CMU_Coin-flipping_Experience
 */

/**
 * Retrieve column i
 */
infix fun FMatrixRMaj.getColumn(i: Int) = CommonOps_FDRM.extractColumn(this, i, null)

/**
 * Replace column i
 */
fun FMatrixRMaj.putColumn(i: Int, col: FMatrixRMaj) = col.data.forEachIndexed { rowi, fl -> set(rowi, i, fl) }

/**
 * Element-wise multiplication (pure)
 */
infix fun FMatrixRMaj.mul(other: FMatrixRMaj) = createLike().also { CommonOps_FDRM.elementMult(this, other, it) }

/**
 * Scalar Multiplication (pure)
 */
operator fun FMatrixRMaj.times(f: Float) = createLike().also { CommonOps_FDRM.scale(f, this, it) }
operator fun Float.times(f: FMatrixRMaj) = f * this