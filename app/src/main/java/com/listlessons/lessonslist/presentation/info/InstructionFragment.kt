package com.listlessons.lessonslist.presentation.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.listlessons.lessonslist.R
import com.listlessons.lessonslist.databinding.FragmentInstructionBinding
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection


class InstructionFragment: Fragment() {


    private var _binding: FragmentInstructionBinding? = null
    private val binding: FragmentInstructionBinding
        get() = _binding ?: throw RuntimeException("FragmentInstructionBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Инструкция"


        val expansionLayout: ExpansionLayout = binding.expansionLayout
        val expansionLayout2: ExpansionLayout = binding.expansionLayout2
        val expansionLayout3: ExpansionLayout = binding.expansionLayout3
        val expansionLayout4: ExpansionLayout = binding.expansionLayout4
        val expansionLayout5: ExpansionLayout = binding.expansionLayout5

        val expansionLayoutCollection = ExpansionLayoutCollection()
        expansionLayoutCollection.add(expansionLayout)
        expansionLayoutCollection.add(expansionLayout2)
        expansionLayoutCollection.add(expansionLayout3)
        expansionLayoutCollection.add(expansionLayout4)
        expansionLayoutCollection.add(expansionLayout5)
        //expansionLayoutCollection.add(expansionLayout3)


        binding.accordionDescription1.text = "1/1 Перед началом работы необходимо добавить студентов." +
                "\n" + "1/2 Группы создаются по желанию и необязательны к добавлению." + "\n" +
                "1/3 После добавления учеников и групп создайте урок, в случае создания урока с главного экрана нажмите и удерживайте дату" +
                " появится экран создания урока дата будет та что Вы выбрали, установите время  начала и конца урока выберите студентов, установите цену и нажмите сохранить." + "\n" +
                "1/4 Так же Вы можете создать урок одиночным нажатием на пустой дате в календаре или в списке уроков по клавише " + "\n" +
                "1/5 Платежи создадутся автоматически по окончанию урока." + "\n" +
                "1/6 Вся информация по колличеству проведенных/запланированных уроков будет отображаться в главном меню приложения доступном в левом верхнем углу экрана вкладки календарь. В остальных разделах заместо главного меню будет стрелка перехода в главное меню."

        binding.accordionDescription2.text = "2/1 Платежи создадутся автоматически по окончанию урока." + "\n" +
                "2/2 Платежи прошедшие успешно отмечаются синим цветом." + "\n" +
                "2/3 Платежи прошедшие не успешно отмечаются красным цветом" + "\n" +
                "2/4 По клику на иконку календаря вид платежей сменится на календарь, где красным будут пометки о долгах, синим об успешных платежах." + "\n" +
                "2/5 Платежи из раздела удаляться не могут, а могут быть удалены только из раздела ученики или уроки."

        binding.accordionDescription3.text = "3/1 Группы можно добавить по клику на иконку плюс в правом углу экрана." + "\n" +
                "3/2 Удалить группу можно выделив ее долгим нажатием и последующим кликом в правом верхнем углу (появится при выделение), вторая иконка справа, первая выделит все группы." + "\n" +
                "3/3 Редактировать группу можно из списка групп путем одиночного нажатия."

        binding.accordionDescription4.text = "4/1 Уроки можно добавить по клику на иконку плюс в правом углу экрана." + "\n" +
                "4/2 После добавления урока его можно отредактировать и добавить скидки на каждого из учеников." + "\n" +
                "4/3 Для урока можно установить напоминание." + "\n" +
                "4/4 При создании урока можно добавлять учеников как по отдельности так и группы учеников, можно добавлять и группы и отдельных учеников." + "\n" +
                "4/5 Удалить урок можно выделив ее долгим нажатием и последующим кликом в правом верхнем углу, вторая иконка справа (появится при выделение), первая выделит все уроки." + "\n" +
                "4/6 При удаление урока с ним удаляться все скидки, платежи, напоминания о нем." + "\n" +
                "4/7 Урок можно дублировать по заданную дату и он будет повторяться в этот же день недели." + "\n" +
                "4/8 Дату и время урока можно изменить." + "\n" +
                "4/8 Урок не может быть создан, если в это время уже запланирован другой урок."

        binding.accordionDescription5.text = "5/1 Учеников можно добавить по клику на иконку плюс в правом углу экрана." + "\n" +
                "5/2 Удалить ученика можно выделив ее долгим нажатием и последующим кликом в правом верхнем углу, вторая иконка справа (появится при выделение), первая выделит всех учеников." + "\n" +
                "5/3 При удаление студента с ним удаляться данные о номерах телефона родственников, заметки о нем, платежи и все сопутствующие данные." + "\n" +
                "5/4 В случае если ученику не хватает денег на оплату урока, то платеж будет создан долгом" + "\n" +
                "5/5 Чтобы списать долг необходимо пополнить баланс ученика." + "\n" +
                "5/6 Баланс ученика не уйдет в отрицательные значения, а будут создаваться долги в разделе платежи."


    }


}