package com.example.lessonslist.presentation.lessons

import android.app.Application
import androidx.lifecycle.*
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.lessons.LessonsListDao
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.group.*
import com.example.lessonslist.domain.lessons.*
import kotlinx.coroutines.launch
import java.util.stream.Collectors


class LessonsListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonsListRepositoryImpl(application)

    private val getLessonsItemListUseCase = GetLessonsListItemUseCase(repository)
    private val deleteLessonsItemUseCase = DeleteLessonsItemUseCase(repository)
    private val editLessonsItemUseCase = EditLessonsItemUseCase(repository)

    val lessonsList = getLessonsItemListUseCase.getLessonsList()
    /*val appDatabase = AppDatabase

    fun getData(): ArrayList<String> {

        val dbLessons = appDatabase.LessonsListDao().getAllLessonsList()
        val arrList: ArrayList<String> = ArrayList()
        dbLessons.let {
            for (item in it){
                arrList.add(item.dateEnd)
            }
            // log(it.get(0).title)
        }
        return arrList
    }*/
   /* private val _offers = MutableLiveData<LessonsItem>()
    val offers: LiveData<LessonsItem> = _offers*/
  // var products: LiveData<List<LessonsItem>> = getLessonsItemListUseCase.getLessonsList()

 //   products.value = getLessonsItemListUseCase.getLessonsList()


        fun deleteLessonsItem(lessonsItem: LessonsItem) {
        viewModelScope.launch {
            deleteLessonsItemUseCase.deleteLessonsItem(lessonsItem)
        }
    }





}
