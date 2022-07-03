package com.example.lessonslist.data.student

import com.example.lessonslist.domain.student.StudentItem

class StudentListMapper {

    fun mapEntityToDbModel(studentItem: StudentItem) = StudentItemDbModel(
        id = studentItem.id,
        name = studentItem.name,
        lastname = studentItem.lastname,
        paymentBalance = studentItem.paymentBalance,
        group = studentItem.group,
        image = studentItem.image,
        notes = studentItem.notes,
        enabled = studentItem.enabled
    )

    fun mapDbModelToEntity(studentItemDbModel: StudentItemDbModel) = StudentItem(
        id = studentItemDbModel.id,
        name = studentItemDbModel.name,
        lastname = studentItemDbModel.lastname,
        paymentBalance = studentItemDbModel.paymentBalance,
        group = studentItemDbModel.group,
        image = studentItemDbModel.image,
        notes = studentItemDbModel.notes,
        enabled = studentItemDbModel.enabled
    )

    fun mapListDbModelToListEntity(list: List<StudentItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
