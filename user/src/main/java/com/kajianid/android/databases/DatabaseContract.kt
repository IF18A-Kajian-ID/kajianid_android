package com.kajianid.android.databases

import android.provider.BaseColumns

object DatabaseContract {
    class KajianColumns : BaseColumns{
        companion object {
            const val TABLE_NAME_KAJIAN = "kajian"
            const val TABLE_NAME_ARTICLES = "articles"
            val ID = "id"
            val KAJIAN_TITLE = "kajian_title"
            val USTADZ_NAME = "ustadz_name"
            val MOSQUE_NAME = "mosque_name"
            val ADDRESS = "address"
            val PLACE = "place"
            val YOUTUBE_LINK = "youtube_link"
            val DESCRIPTION = "description"
            val IMG_RESOURCE = "img_resource"
            val DATE_ANNOUNCE = "date_announce"
            val DATE_DUE = "date_due"
            val POST_DATE = "post_date"
            val CONTENT = "content"
            val HAS_IMG = "has_img"
            val IMGURL = "imgurl"
        }
    }
}