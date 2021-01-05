package com.kajianid.android.databases

import android.database.Cursor
import com.kajianid.android.data.Kajian

object MappingHelper {
    fun mapCursorToArrayList(kajianCursor: Cursor?): ArrayList<Kajian> {
        var kajian2 = ArrayList<Kajian>()
        kajianCursor?.apply {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.ID))
                val kajian_title = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.KAJIAN_TITLE))
                val ustadz_name = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.USTADZ_NAME))
                val mosque_name = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.MOSQUE_NAME))
                val address = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.ADDRESS))
                val place = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.PLACE))
                val youtube_link = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.YOUTUBE_LINK))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.DESCRIPTION))
                val img_resource = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.IMG_RESOURCE))
                val date_announce = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.DATE_ANNOUNCE))
                val date_due = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.DATE_DUE))
                val kajian = Kajian()
                kajian.id = id
                kajian.title = kajian_title
                kajian.ustadzName = ustadz_name
                kajian.mosque = mosque_name
                kajian.address = address
                kajian.place = place
                kajian.youtubelink = youtube_link
                kajian.description = description
                kajian.imgResource = img_resource
                kajian.dateAnnounce = date_announce
                kajian.date = date_due
                kajian2.add(kajian)
            }
        }
        return kajian2
    }
    fun mapCursorToObject(kajianCursor: Cursor?): Kajian {
        var kajian = Kajian()
        kajianCursor?.apply {
            moveToFirst()
            val id = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.ID))
            val kajian_title = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.KAJIAN_TITLE))
            val ustadz_name = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.USTADZ_NAME))
            val mosque_name = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.MOSQUE_NAME))
            val address = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.ADDRESS))
            val place = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.PLACE))
            val youtube_link = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.YOUTUBE_LINK))
            val description = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.DESCRIPTION))
            val img_resource = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.IMG_RESOURCE))
            val date_announce = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.DATE_ANNOUNCE))
            val date_due = getString(getColumnIndexOrThrow(DatabaseContract.KajianColumns.DATE_DUE))
            kajian.id = id
            kajian.title = kajian_title
            kajian.ustadzName = ustadz_name
            kajian.mosque = mosque_name
            kajian.address = address
            kajian.place = place
            kajian.youtubelink = youtube_link
            kajian.description = description
            kajian.imgResource = img_resource
            kajian.dateAnnounce = date_announce
            kajian.date = date_due
        }
        return kajian
    }
}