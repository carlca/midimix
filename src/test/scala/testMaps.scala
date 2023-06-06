class testMaps {
 /** 
  * mTracks[Int, Int]
  * 
  * Map[Int, Int] is an unsorted type which won't matter for operation.
  * If the contents need to be eyeballed, use (SortedMap.from(mTracks)).toString
  *
  *  mTracks should look like this...
  * {16=0, 17=0, 18=0, 19=0, 20=1, 21=1, 22=1, 23=1, 24=2, 25=2, 26=2, 27=2, 28=3, 29=3, 30=3, 31=3,
  *  46=4, 47=4, 48=4, 49=4, 50=5, 51=5, 52=5, 53=5, 54=6, 55=6, 56=6, 57=6, 58=7, 59=7, 60=7, 61=7,
  *  62=255}
  *
  * We are getting this at the moment...
  * (16=0, 17=0, 18=0, 19=0, 20=1, 21=1, 22=1, 23=1, 24=2, 25=2, 26=2, 27=2, 28=3, 29=3, 30=3, 31=3,
  *  46=4, 47=4, 48=4, 49=4, 50=5, 51=5, 52=5, 53=5, 54=6, 55=6, 56=6, 57=6, 58=7, 59=7, 60=7, 61=7,
  *  62=255)
  *
  *  Conclusion: initTrackMap works as intended. Unit tests to follow!
  *
  */  

 /**
  * mTypes: Map[Int, Int]
  * 
  * Map[Int, Int] is an unsorted type which won't matter for operation.
  * If the contents need to be eyeballed, use (SortedMap.from(mTypes)).toString
  *
  * mTypes should look like this, when sorted...
  * {16=0, 17=1, 18=2, 19=3, 20=0, 21=1, 22=2, 23=3, 24=0, 25=1, 26=2, 27=3, 28=0, 29=1, 30=2, 31=3,
  *  46=0, 47=1, 48=2, 49=3, 50=0, 51=1, 52=2, 53=3, 54=0, 55=1, 56=2, 57=3, 58=0, 59=1, 60=2, 61=3,
  *  62=3}
  *
  * We are getting this at the moment...
  * (16=0, 17=1, 18=2, 19=3, 20=0, 21=1, 22=2, 23=3, 24=0, 25=1, 26=2, 27=3, 28=0, 29=1, 30=2, 31=3,
  *  46=0, 47=1, 48=2, 49=3, 50=0, 51=1, 52=2, 53=3, 54=0, 55=1, 56=2, 57=3, 58=0, 59=1, 60=2, 61=3,
  *  62=3)
  *
  * Conclusion: mTypes works as intended. Unit tests to follow!
  * 
  */
  
} 
