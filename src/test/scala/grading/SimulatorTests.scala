package grading

abstract class SimulatorTests extends SimulatorSampleTests {

  for (time <- List(1, 2019))
    network4x4Tests(time)

  for ((width, height, count) <- List((10, 10, 100), (12, 34, 5)))
    variousNetworkSizesTest(width, height, count)

  for (filename <- List("10-10-1-100", "10-10-3-1000", "50-100-3-1000", "50-100-5-1000"))
    completeSimulation(filename)
}
