package Semester.exam.Java_Project;

import Semester.exam.Java_Project.entity.Category;
import Semester.exam.Java_Project.entity.Department;
import Semester.exam.Java_Project.repository.CategoryRepository;
import Semester.exam.Java_Project.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;

    public DataInitializer(DepartmentRepository departmentRepository, CategoryRepository categoryRepository) {
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() <= 1) {
            Department pwd = departmentRepository.save(createDept("Public Works Department"));
            Department water = departmentRepository.save(createDept("Water & Hydrology Board"));
            Department sanitation = departmentRepository.save(createDept("Sanitation & Environmental Services"));
            Department electrical = departmentRepository.save(createDept("Municipal Grid & Energy"));

            if (categoryRepository.count() == 0) {
                categoryRepository.save(createCategory("Roads & Infrastructure", 3, pwd));
            } else {
                Category c1 = categoryRepository.findById(1L).orElse(null);
                if (c1 != null) {
                    c1.setName("Roads & Infrastructure");
                    c1.setSlaDays(3);
                    c1.setDepartment(pwd);
                    categoryRepository.save(c1);
                }
            }

            if (!categoryRepository.existsById(2L)) {
                categoryRepository.save(createCategory("Water Supply & Sewage", 2, water));
            }
            if (!categoryRepository.existsById(3L)) {
                categoryRepository.save(createCategory("Garbage & Solid Waste", 1, sanitation));
            }
            if (!categoryRepository.existsById(4L)) {
                categoryRepository.save(createCategory("Streetlights & Electrical", 2, electrical));
            }
        }
    }

    private Department createDept(String name) {
        Department d = new Department();
        d.setName(name);
        return d;
    }

    private Category createCategory(String name, int slaDays, Department dept) {
        Category c = new Category();
        c.setName(name);
        c.setSlaDays(slaDays);
        c.setDepartment(dept);
        return c;
    }
}
