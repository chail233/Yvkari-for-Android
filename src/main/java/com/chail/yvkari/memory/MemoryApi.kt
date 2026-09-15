package com.chail.yvkari.memory

import com.chail.yvkari.chat.api.retrofit

val memoryApi: MemoryApiService  = retrofit.create(MemoryApiService::class.java)

