package java.lang

object ProcessSuite extends tests.Suite {
    test("ls") {
       new ProcessBuilder(Array("ls") :_*).start()
    }
}
