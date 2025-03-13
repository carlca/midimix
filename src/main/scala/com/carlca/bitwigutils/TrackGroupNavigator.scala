package com.carlca.bitwigutils

import com.bitwig.extension.controller.api.CursorTrack
import com.bitwig.extension.controller.api.Track

/**
 * Wraps a track cursor to provide group navigation.
 */
class TrackGroupNavigator(cursorTrack: CursorTrack):
  private val parentTrack: Track = cursorTrack.createParentTrack(0, 0)
  cursorTrack.isGroup().markInterested()
  cursorTrack.isGroupExpanded().markInterested()
  parentTrack.isGroupExpanded().markInterested()

  /**
   * Navigates groups when called.
   *
   * If the current track is a group, then it will be unfolded and the first child track will be
   * selected.
   *
   * If the current track is not a group, then it will attempt to navigate up to the parent track if
   * one exists. If fold is true, then the parent group is automatically folded when navigating out
   * of the group.
   *
   * @param fold When true, a group is folded upon exiting the group. When false, the group will
   *             remain unfolded on exiting.
   */
  def navigateGroups(fold: Boolean): Unit =
    if cursorTrack.isGroup().get() then
      cursorTrack.isGroupExpanded().set(true)
      cursorTrack.selectFirstChild()
    else
      if parentTrack == null then return
      cursorTrack.selectParent()
      if fold then
        cursorTrack.isGroupExpanded().set(false)
