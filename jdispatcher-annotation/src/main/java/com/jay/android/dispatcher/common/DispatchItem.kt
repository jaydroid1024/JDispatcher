package com.jay.android.dispatcher.common

import com.google.gson.annotations.SerializedName
import com.jay.android.dispatcher.annotation.Dimension
import com.jay.android.dispatcher.annotation.Priority

/**
 * 用于描述每个 Dispatch 的实体类
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
class DispatchItem(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("priority")
    var priority: Int = Priority.MEDIUM_DEFAULT,
    @SerializedName("dimension")
    var dimension: Int = Dimension.DIMENSION_DEFAULT,
    @SerializedName("className")
    var className: String = "",
    @SerializedName("dependencies")
    var dependencies: Array<String> = arrayOf(),
    @SerializedName("description")
    var description: String = ""
) : Comparable<DispatchItem> {

    var instance: IDispatch? = null
    var extraParam: HashMap<String, String>? = null
    var processName: String = ""
    var time: Long = 0

    override fun compareTo(other: DispatchItem): Int {
        //按照优先级排序,
        return priority.compareTo(other.priority)
    }

    override fun toString(): String {
        return "DispatchItem(name='$name', priority=$priority, dimension=$dimension, className='$className', dependencies=${dependencies.contentToString()}, description='$description', instance=$instance, extraParam=$extraParam)"
    }

    companion object {

        @JvmStatic
        fun build(): DispatchItem {
            return DispatchItem("", 0, 0, "", arrayOf(), "")
        }

        @JvmStatic
        fun build(
            name: String,
            priority: Int,
            dimension: Int,
            className: String,
            dependencies: Array<String>,
            description: String
        ): DispatchItem {
            return DispatchItem(name, priority, dimension, className, dependencies, description)
        }
    }


}