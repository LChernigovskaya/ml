import java.io.*
import java.util.Arrays
import java.util.Vector

class AffinityPropagation(dumpFactor: Double?) {
    private val n = 196591
    private val dumpFactor = dumpFactor ?: 0.5
    private var edges = Vector<Vector<Edge>>()

    private val exemplars: IntArray
        get() {
            val exemplars = IntArray(n)
            for (i in 0 until n) {
                var idx = -1
                var argMax = -Double.MAX_VALUE
                for (edge in edges.elementAt(i)) {
                    if (edge.r + edge.a > argMax) {
                        argMax = edge.r + edge.a
                        idx = edge.idx
                    }
                }
                exemplars[i] = idx
            }
            return exemplars
        }

    internal inner class Edge(var idx: Int, var w: Int) {
        var a = 0.0
        var r = 0.0
    }

    fun exec(file: String, nEpochs: Int) {
        for (i in 0 until n) {
            edges.add(Vector())
        }

        readFromFile(file)

        var countEq = 0
        var newExemplars = IntArray(n)
        var curExemplars = IntArray(n)
        var wasEq = false
        for (i in 0 until nEpochs) {
            println(i)
            updateParameters()
            curExemplars = newExemplars
            newExemplars = exemplars

            var eq = true
            for (j in 0 until n) {
                if (curExemplars[j] != newExemplars[j]) {
                    eq = false
                    break
                }
            }
            if (eq && wasEq) countEq++
            if (countEq > 5) break
            wasEq = eq;
        }
        print("Number of clusters: ")
        println(Arrays.stream(curExemplars).distinct().toArray().size)

        printToFile(curExemplars)

    }

    private fun readFromFile(file: String) {
        File(file).forEachLine {
            val str = it.split("\t")
            val from = Integer.parseInt(str[0])
            val to = Integer.parseInt(str[1])
            edges.elementAt(from).add(Edge(to, 1))
        }
        for (i in 0 until n) {
            edges.elementAt(i).add(Edge(i, -1))
        }
    }

    private fun updateParameters() {
        val sumMax = DoubleArray(n)
        for (i in 0 until n) {
            var max = -Double.MAX_VALUE
            var max2 = -Double.MAX_VALUE
            var idxMax = -1
            for (edge in edges.elementAt(i)) {
                if (edge.a + edge.w > max) {
                    max2 = max
                    max = edge.a + edge.w
                    idxMax = edge.idx
                }
            }
            for (edge in edges.elementAt(i)) {
                val curMax = if (idxMax == edge.idx) max2 else max
                edge.r = dumpFactor * edge.r + (1 - dumpFactor) * (edge.w - curMax)
                sumMax[edge.idx] += Math.max(0.0, edge.r)
            }
        }
        for (i in 0 until n) {
            for (edge in edges.elementAt(i)) {
                val k = edge.idx
                val kVector = edges.elementAt(k)
                val rkk = kVector.elementAt(kVector.size - 1).r
                val sum = sumMax[k] - Math.max(0.0, edge.r) - Math.max(0.0, rkk)
                if (i != k) {
                    edge.a = dumpFactor * edge.a + (1 - dumpFactor) * Math.min(0.0, rkk + sum)
                } else {
                    edge.a = dumpFactor * edge.a + (1 - dumpFactor) * sum
                }
            }
        }
    }

    private fun printToFile(exemplars: IntArray) {
        File("answer.txt").printWriter().use { out ->
            for (exemplar in exemplars) {
                out.println(exemplar.toString())
            }
        }
    }
}
