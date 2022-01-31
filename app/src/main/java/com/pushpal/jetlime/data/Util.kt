package com.pushpal.jetlime.data

import androidx.compose.runtime.snapshots.SnapshotStateList

fun SnapshotStateList<JetLimeItem>.initBasic() {
  addAll(FakeData.simpleJetLimeItems)
}

fun SnapshotStateList<JetLimeItem>.initAnimated() {
  addAll(FakeData.animatedJetLimeItems)
}

fun SnapshotStateList<JetLimeItem>.initNewLook() {
  addAll(FakeData.simpleJetLimeItems)
}