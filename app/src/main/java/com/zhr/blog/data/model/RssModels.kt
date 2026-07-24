package com.zhr.blog.data.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RssFeed(
    @field:Element(name = "channel")
    var channel: Channel? = null
)

@Root(name = "channel", strict = false)
data class Channel(
    @field:ElementList(name = "item", inline = true, required = false)
    var items: List<Item>? = null
)

@Root(name = "item", strict = false)
data class Item(
    @field:Element(name = "title")
    var title: String = "",
    @field:Element(name = "link")
    var link: String = "",
    @field:Element(name = "description", required = false)
    var description: String = "",
    @field:Element(name = "pubDate", required = false)
    var pubDate: String = ""
)