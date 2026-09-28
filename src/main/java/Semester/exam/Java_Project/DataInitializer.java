package Semester.exam.Java_Project;

import Semester.exam.Java_Project.entity.*;
import Semester.exam.Java_Project.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final GrievanceRepository grievanceRepository;
    private final EscalationRepository escalationRepository;

    public DataInitializer(DepartmentRepository departmentRepository,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository,
                           GrievanceRepository grievanceRepository,
                           EscalationRepository escalationRepository) {
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.grievanceRepository = grievanceRepository;
        this.escalationRepository = escalationRepository;
    }

    @Override
    public void run(String... args) {
        // Seed Users (if not exist)
        if (!userRepository.existsByUsername("citizen")) {
            User citizen = new User();
            citizen.setUsername("citizen");
            citizen.setPassword("citizen123");
            citizen.setFullName("Ravi Kumar");
            citizen.setRole(Role.CITIZEN);
            userRepository.save(citizen);
        }

        if (!userRepository.existsByUsername("senior")) {
            User senior = new User();
            senior.setUsername("senior");
            senior.setPassword("senior123");
            senior.setFullName("Senior Officer Priya");
            senior.setRole(Role.SENIOR_OFFICER);
            userRepository.save(senior);
        }

        // --- Seed Departments + Categories (only if empty) ---
        if (categoryRepository.count() == 0) {
            Department pwd      = departmentRepository.save(createDept("Public Works Department"));
            Department water    = departmentRepository.save(createDept("Water & Hydrology Board"));
            Department sanit    = departmentRepository.save(createDept("Sanitation & Environmental Services"));
            Department electric = departmentRepository.save(createDept("Municipal Grid & Energy"));

            categoryRepository.save(createCategory("Roads & Infrastructure",    3, pwd));
            categoryRepository.save(createCategory("Water Supply & Sewage",     2, water));
            categoryRepository.save(createCategory("Garbage & Solid Waste",     1, sanit));
            categoryRepository.save(createCategory("Streetlights & Electrical", 2, electric));
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
