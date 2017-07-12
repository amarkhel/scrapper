package webscrapper.parser

object d {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  import scala.io.Source
  val html = Source.fromURL("http://mafiaonline.ru/api/api.php?action=log&id=3705415&param=log", "UTF-8")
                                                  //> html  : scala.io.BufferedSource = non-empty iterator
  val s = html.mkString                           //> s  : String = {"r":"ok","log":{"id":3705415,"end":1484861704,"ul":"ul2","vip
                                                  //| ":false,"result":"3","players":[{"state":2,"role":1,"nick":"Vikki_Vi","id":3
                                                  //| 49089,"sex":"w"},{"state":1,"role":3,"nick":"\u0432\u043e\u0440\u0438\u0448\
                                                  //| u043a\u0430","id":373215,"sex":"w"},{"state":2,"role":1,"nick":"\u0422\u043e
                                                  //| \u0448\u0438\u0447","id":524743,"sex":"m"},{"state":2,"role":0,"nick":"isitr
                                                  //| eal","id":147820,"sex":"m"},{"state":2,"role":0,"nick":"\u041b\u0430\u043c\u
                                                  //| 0438\u044f","id":528334,"sex":"w"},{"state":0,"role":0,"nick":"\u041a\u0430\
                                                  //| u0440\u0442\u043e\u0448\u043a\u0430","id":396615,"sex":"m"},{"state":1,"role
                                                  //| ":0,"nick":"jAr1kS","id":526175,"sex":"m"},{"state":1,"role":4,"nick":"the V
                                                  //| iolet","id":-1,"sex":"p"},{"state":4,"role":0,"nick":"Grace_K","id":495691,"
                                                  //| sex":"w"},{"state":4,"role":2,"nick":"Minerva","id":531490,"sex":"w"},{"stat
                                                  //| e":1,"role":5,"nick":"Inn-Ah","id":367375,"sex":"w"},{"state":0,"role":7,"ni
                                                  //| ck":"Luksera","id":17967
                                                  //| Output exceeds cutoff limit.
  println(s)                                      //> {"r":"ok","log":{"id":3705415,"end":1484861704,"ul":"ul2","vip":false,"resul
                                                  //| t":"3","players":[{"state":2,"role":1,"nick":"Vikki_Vi","id":349089,"sex":"w
                                                  //| "},{"state":1,"role":3,"nick":"\u0432\u043e\u0440\u0438\u0448\u043a\u0430","
                                                  //| id":373215,"sex":"w"},{"state":2,"role":1,"nick":"\u0422\u043e\u0448\u0438\u
                                                  //| 0447","id":524743,"sex":"m"},{"state":2,"role":0,"nick":"isitreal","id":1478
                                                  //| 20,"sex":"m"},{"state":2,"role":0,"nick":"\u041b\u0430\u043c\u0438\u044f","i
                                                  //| d":528334,"sex":"w"},{"state":0,"role":0,"nick":"\u041a\u0430\u0440\u0442\u0
                                                  //| 43e\u0448\u043a\u0430","id":396615,"sex":"m"},{"state":1,"role":0,"nick":"jA
                                                  //| r1kS","id":526175,"sex":"m"},{"state":1,"role":4,"nick":"the Violet","id":-1
                                                  //| ,"sex":"p"},{"state":4,"role":0,"nick":"Grace_K","id":495691,"sex":"w"},{"st
                                                  //| ate":4,"role":2,"nick":"Minerva","id":531490,"sex":"w"},{"state":1,"role":5,
                                                  //| "nick":"Inn-Ah","id":367375,"sex":"w"},{"state":0,"role":7,"nick":"Luksera",
                                                  //| "id":179676,"sex":"w"},{
                                                  //| Output exceeds cutoff limit.
}