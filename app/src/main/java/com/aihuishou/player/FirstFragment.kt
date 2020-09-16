package com.aihuishou.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.aihuishou.player.media.JZMediaIjk
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    var url = "http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        jz_video.setUp(url, "饺子变心", JzvdStd.SCREEN_NORMAL, JZMediaIjk::class.java)

        Glide.with(this)
            .load("http://jzvd-pic.nathen.cn/jzvd-pic/1bb2ebbe-140d-4e2e-abd2-9e7e564f71ac.png")
            .into(jz_video.posterImageView)
    }
}