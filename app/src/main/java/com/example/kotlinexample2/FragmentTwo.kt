package com.example.kotlinexample2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class FragmentTwo : Fragment(){


    private val viewModel:UserViewModel by activityViewModels()
    private lateinit var textView:TextView

    // GET request
    private var url = "http://api.zippopotam.us/us/"
    private val client = AsyncHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_two, container, false)
        textView = view.findViewById(R.id.text_view_two)

        // call get information, which returns us a live data
        // so that we can observe on the this live data to get changes
        // then we want to display information in the UI

//        viewModel.getInformation().observe(viewLifecycleOwner, object: Observer<UserInformation> {
//
//            override fun onChanged(t: UserInformation?) {
//                if ( t!= null){
//                    textView.text = t.name + " : " + t.zipcode
//                }
//            }
//        })


        // let -> scope functions
        // the context object is available as an argument called it
        // the return value is a lambda result

        // ?. -> safe call operator

        // let only works with non null objects
        viewModel.getInformation().observe(viewLifecycleOwner, Observer {
            userInfo -> userInfo?.let{
            //textView.text = it.name + " : " + it.zipcode
            var city = ""
            val name = it.name

            client.get(url + it.zipcode, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    var json= JSONObject(String(responseBody!!))
                    city = json.getJSONArray("places").getJSONObject(0).getString("place name")
                    Log.println(Log.INFO, "city: ", city)

                    if(city.isNotEmpty()) {
                        textView.text = "$name : $city"
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    Log.e("api error", String(responseBody!!));
                    textView.text = "Error: Invalid Zip Code"
                }
            })
        }
        })

        return view
    }
}