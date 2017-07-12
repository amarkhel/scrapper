package com.amarkhel.interview

import org.junit.runner.RunWith
import org.scalatest.Matchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import com.amarkhel.interview.InterviewApp.Paper
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.time.temporal.TemporalUnit

@RunWith(classOf[JUnitRunner])
class InterviewTest extends FunSuite with Matchers {

  test("size should return correct value"){
    val papers = List(Paper("test", LocalDate.of(2015, 12, 11)), Paper("Andrey", LocalDate.of(2015, 12, 10)))
    InterviewApp.sizeOfPapers(papers) should be (2)
  }
  
  test("sorting by title should return correct value"){
    val papers = List(Paper("test", LocalDate.of(2015, 12, 11)), Paper("Andrey", LocalDate.of(2015, 12, 10)))
    InterviewApp.sortPapersByTitle(papers).head.title should be ("Andrey")
  }
  
  test("sorting by publication date should return correct value"){
    val papers = List(Paper("test", LocalDate.of(2015, 12, 11)), Paper("Andrey", LocalDate.of(2015, 12, 10)))
    val result = InterviewApp.sortPapersByPublicationDate(papers)
    result.size should be (2)
    result.head.title should be ("Andrey")
  }
  
  test("reverse sorting by publication date should return correct value"){
    val papers = List(Paper("test", LocalDate.of(2015, 12, 11)), Paper("Andrey", LocalDate.of(2015, 12, 10)))
    val result = InterviewApp.reverseSortPapersByPublicationDate(papers)
    result.size should be (2)
    result.head.title should be ("test")
  }
}