package com.example.lessonslist.data.student

import com.example.buylistapplication.domain.StudentItem


class StudentListMapper {

    fun mapEntityToDbModel(studentItem: StudentItem) = StudentItemDbModel(
        id = studentItem.id,
        name = studentItem.name,
        lastname = studentItem.lastname,
        paymentBalance = studentItem.paymentBalance,
        group = studentItem.group,
        notes = studentItem.notes
    )

    fun mapDbModelToEntity(studentItemDbModel: StudentItemDbModel) = StudentItem(
        id = studentItemDbModel.id,
        name = studentItemDbModel.name,
        lastname = studentItemDbModel.lastname,
        paymentBalance = studentItemDbModel.paymentBalance,
        group = studentItemDbModel.group,
        notes = studentItemDbModel.notes
    )

    fun mapListDbModelToListEntity(list: List<StudentItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
