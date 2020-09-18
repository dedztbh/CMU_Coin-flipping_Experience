package operator

import org.ejml.data.FMatrixRMaj
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
    override fun printResult() {
        eval(
            // a 2^n by n matrix
            FMatrixRMaj(allStatesFloat(N))
        ).print()
    }
}
