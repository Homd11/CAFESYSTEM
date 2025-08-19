package Interfaces;

import Core.Student;

public interface IStudentRepositor {
        void save(Student student);              // INSERT (مع إنشاء LoyaltyAccount لو لسه)
        void update(Student student);            // UPDATE name/code وربط account
        void delete(int id);                     // DELETE (خلي بالك من القيود)
        Student findById(int id);                // SELECT + JOIN
        Student findByCode(String code);         // SELECT + JOIN
        java.util.List<Student> findAll();       // SELECT + JOIN
    }


