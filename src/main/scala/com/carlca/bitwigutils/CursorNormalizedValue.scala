package com.carlca.bitwigutils

import com.bitwig.extension.callback.DoubleValueChangedCallback
import com.bitwig.extension.controller.api.CursorDevice
import com.bitwig.extension.controller.api.CursorRemoteControlsPage
import com.bitwig.extension.controller.api.CursorTrack
import com.bitwig.extension.controller.api.DeviceBank
import com.bitwig.extension.controller.api.TrackBank
import com.carlca.utils.MathUtil

import scala.collection.mutable

/**
 * Wraps a cursor and bank in order to provide a normalized 0-1 value for the cursor position.
 *
 * Sends -1.0 if there are no items in the bank.
 */
class CursorNormalizedValue:
  private val observers = mutable.Set[DoubleValueChangedCallback]()
  private var cursorIndex: Int = -1
  private var cursorCount: Int = -1

  /**
   * Creates a wrapper for a track cursor.
   *
   * @param cursorTrack The track cursor to wrap.
   * @param trackBank   The bank for the cursor.
   */
  def this(cursorTrack: CursorTrack, trackBank: TrackBank) =
    this()
    trackBank.channelCount().markInterested()
    trackBank.channelCount().addValueObserver(new com.bitwig.extension.callback.IntegerValueChangedCallback { // Explicit callback
      override def valueChanged(count: Int): Unit = {
        setCursorCount(count)
      }
    })

    cursorTrack.position().markInterested()
    cursorTrack.position().addValueObserver(new com.bitwig.extension.callback.IntegerValueChangedCallback { // Explicit callback
      override def valueChanged(index: Int): Unit = {
        setCursorIndex(index)
      }
    })

  /**
   * Creates a wrapper for a device cursor.
   *
   * @param cursorDevice The device cursor to wrap.
   * @param deviceBank   The bank for the cursor.
   */
  def this(cursorDevice: CursorDevice, deviceBank: DeviceBank) =
    this()
    deviceBank.itemCount().markInterested()
    deviceBank.itemCount().addValueObserver(new com.bitwig.extension.callback.IntegerValueChangedCallback {  // Explicit callback
      override def valueChanged(count: Int): Unit = {
        setCursorCount(count)
      }
    })

    cursorDevice.position().markInterested()
    cursorDevice.position().addValueObserver(new com.bitwig.extension.callback.IntegerValueChangedCallback { // Explicit callback
      override def valueChanged(index: Int): Unit = {
        setCursorIndex(index)
      }
    })

  /**
   * Creates a wrapper for remote controls pages.
   *
   * @param cursorRemoteControlsPage The remote control pages to wrap.
   */
  def this(cursorRemoteControlsPage: CursorRemoteControlsPage) =
    this()
    cursorRemoteControlsPage.pageCount().markInterested()
    cursorRemoteControlsPage.pageCount().addValueObserver(new com.bitwig.extension.callback.IntegerValueChangedCallback { // Explicit callback
      override def valueChanged(count: Int): Unit = {
        setCursorCount(count)
      }
    })

    cursorRemoteControlsPage.selectedPageIndex().markInterested()
    cursorRemoteControlsPage.selectedPageIndex().addValueObserver(new com.bitwig.extension.callback.IntegerValueChangedCallback { // Explicit callback
      override def valueChanged(index: Int): Unit = {
        setCursorIndex(index)
      }
    })

  /**
   * Adds and observer for the wrapped value.
   *
   * @param callback The observer callback.
   * @return True if this set did not already contain the specified element.
   */
  def addValueObserver(callback: DoubleValueChangedCallback): Boolean =
    observers.add(new com.bitwig.extension.callback.DoubleValueChangedCallback { // Explicit callback
      override def valueChanged(newValue: Double): Unit = {
        callback.valueChanged(newValue)
      }
    })

  /** Handles when the cursor index changes. */
  private def setCursorIndex(index: Int): Unit =
    cursorIndex = index
    updateCursorFeedback()

  /** Handles when the bank item count changes. */
  private def setCursorCount(count: Int): Unit =
    cursorCount = count
    updateCursorFeedback()

  /** Generates the normalized value and notifies observers. */
  private def updateCursorFeedback(): Unit =
    if cursorCount < 1 || cursorIndex < 0 then
      notifyObservers(-1)
      return

    if cursorCount < 2 then
      notifyObservers(0)
      return

    val normalized = cursorIndex.toDouble / (cursorCount - 1.0)
    notifyObservers(MathUtil.clamp(normalized, 0.0, 1.0))

  private def notifyObservers(value: Double): Unit =
    observers.foreach(observer => observer.valueChanged(value))
