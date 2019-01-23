object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val ap = AffinityPropagation( 0.5)
        ap.exec("Gowalla_edges.txt", 10)
    }
}
