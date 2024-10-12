package com.example.ElearningTLU.Services.TeachingScheduleService;

import com.example.ElearningTLU.Dto.Lop;
import com.example.ElearningTLU.Dto.Request.GradeStudentRequest;
import com.example.ElearningTLU.Dto.Response.ClassRoomDetailResponse;
import com.example.ElearningTLU.Dto.Response.GradeStudentResponse;
import com.example.ElearningTLU.Dto.Response.SemesterGroupResponse;
import com.example.ElearningTLU.Dto.ScheduleDto;
import com.example.ElearningTLU.Dto.TeacherResponse;
import com.example.ElearningTLU.Entity.*;
import com.example.ElearningTLU.Entity.Class;
import com.example.ElearningTLU.Repository.*;
import com.example.ElearningTLU.Services.ClassRoomService.RoomService;
import com.example.ElearningTLU.Utils.RegisterUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeachingScheduleService implements TeachingScheduleServiceImpl {
    @Autowired
    private TimeTableRepository timeTableRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ClassRoomStudentRepository classRoomStudentRepository;

    @Autowired
    private SemesterGroupRepository semesterGroupRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseGardeRepository courseGardeRepository;

    @Autowired
    private StatisticsStudentRepository statisticsStudentRepository;

    @Autowired
    private CourseSemesterGroupRepository courseSemesterGroupRepository;

    @Autowired
    private RegisterUtils registerUtils;
    private ModelMapper mapper = new ModelMapper();


    public ResponseEntity<?> getScheduleBySemester(String id, String semester)
    {
        Person person = this.getPerSon(id);
//        List<TimeTable > teachingSchedules = this.teachingScheduleRepository.findByPersonIdAndSemesterGroupId(person.getPersonId(),semester);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostAuthorize("returnObject.userName== authentication.name")
    public Person getPerSon(String id)
    {
        Person person = this.personRepository.findByUserNameOrPersonId(id).get();
        return person;

    }
    public ResponseEntity<?> getTeacherBySemester(String id)
    {
        List<Semester_Group> semesterGroupList = this.registerUtils.semesterGroupList(id);
        List<TeacherResponse> responseList = new ArrayList<>();
        List<Person> personList = this.personRepository.findAllPersonByRole(Role.TEACHER.name()).get();
        for(Person p: personList)
        {
            TeacherResponse response = new TeacherResponse();
            response = this.mapper.map(p,TeacherResponse.class);
            response.setDepartmentId(p.getDepartment().getDepartmentId());
            for(Semester_Group semesterGroup : semesterGroupList)
            {
                List<Class> classList =this.classRepository.getAllClassBySemesterId(semesterGroup.getSemesterGroupId());
                for(Class aClass : classList)
                {
                    if(aClass.getTeacher().getPersonId().equals(p.getPersonId()))
                    {
                        ScheduleDto scheduleDto = this.mapper.map(aClass,ScheduleDto.class);
                        scheduleDto.setRoomId(aClass.getRoom().getRoomId());
                        response.getTeachingScheduleList().add(scheduleDto);
                    }
                }
            }
            responseList.add(response);
        }
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

   public ResponseEntity<?> getStudentListByClassRoom(String TeacherId,String ClassRoomId,String semesterId)
    {
        Person person = this.personRepository.findByUserNameOrPersonId(TeacherId).get();
        Teacher teacher = this.mapper.map(person,Teacher.class);
        LocalDate date = LocalDate.now();
        List<Class_Student> ListStudent= new ArrayList<>();
        Semester_Group semesterGroup = this.semesterGroupRepository.findById(semesterId).get();
        if(semesterGroup.getTimeDangKyHoc().until(date,ChronoUnit.DAYS)<0)
        {
            return new ResponseEntity<>("Khong Co Thong Tin",HttpStatus.BAD_REQUEST);
        }

            for(Class aClass : teacher.getListClasses())
            {
            if(aClass.getClassRoomId().equals(ClassRoomId)&& aClass.getCourseSemesterGroup().getSemesterGroup().equals(semesterGroup))
                {
                    ClassRoomDetailResponse classRoomDetailResponse= new ClassRoomDetailResponse();
                    classRoomDetailResponse.setClassRoomId(aClass.getClassRoomId());
                    classRoomDetailResponse.setClassRoomName(aClass.getName());
                    classRoomDetailResponse.setSemesterGroupId(aClass.getCourseSemesterGroup().getSemesterGroup().getSemesterGroupId());
                    classRoomDetailResponse.setStart(aClass.getStart());
                    classRoomDetailResponse.setFinish(aClass.getFinish());
    //                List<GradeStudentResponse> studentResponses= new ArrayList<>();
                    for(Class_Student classRoomStudent: aClass.getClassStudents())
                    {
                        GradeStudentResponse studentResponse= new GradeStudentResponse();
                        studentResponse.setStudentId(classRoomStudent.getStudent().getPersonId());
                        studentResponse.setStudentName(classRoomStudent.getStudent().getFullName());
                        studentResponse.setBirthDay(classRoomStudent.getStudent().getDateOfBirth());
                        studentResponse.setMidScore(classRoomStudent.getMidScore());
                        studentResponse.setEndScore(classRoomStudent.getEndScore());
//                        studentResponse.setStatus(StatusCourse.DANGHOC.name());
                        classRoomDetailResponse.getStudentList().add(studentResponse);
                    }
                    return new ResponseEntity<>(classRoomDetailResponse,HttpStatus.OK);

                }
            }
        return new ResponseEntity<>("Khong Co Thong Tin",HttpStatus.BAD_REQUEST);

    }
//update score for Student
    public ResponseEntity<?> updateStudentScore(String id, GradeStudentRequest gradeStudentRequest)
    {
        Semester_Group semesterGroup = this.semesterGroupRepository.findById(gradeStudentRequest.getSemesterId()).get();
        if(LocalDate.now().until(semesterGroup.getFinish(),ChronoUnit.DAYS)<0)
        {
            return new ResponseEntity<>("Khong The Chinh Sua Diem",HttpStatus.BAD_REQUEST);
        }

        Person person = this.personRepository.findByUserNameOrPersonId(id).get();
        Teacher teacher = this.mapper.map(person,Teacher.class);
        List<Class> classList = new ArrayList<>();
        Course course = new Course();
//        this.classRoomRepository.fi
        Student student= this.mapper.map(this.personRepository.findByUserNameOrPersonId(gradeStudentRequest.getStudentId()).get(),Student.class);
        boolean CheckClassRoom= true;
        //lay danh sach lop ma giao vien dang day cuar lop do
        for(Class aClass : teacher.getListClasses())
        {
            if(aClass.getCourseSemesterGroup().getSemesterGroup().equals(semesterGroup) && aClass.getClassRoomId().equals(gradeStudentRequest.getClassRoomId()))
            {
                System.out.println("classRoomId:" + aClass.getClassRoomId());
                course=this.courseRepository.findByCourseId(aClass.getCourseSemesterGroup().getCourse().getCourseId()).get();
                classList.add(aClass);
                CheckClassRoom=false;
            }
        }
        if (CheckClassRoom)
        {
            return new ResponseEntity<>("Ma Giao Vien "+teacher.getPersonId()+" Khong Duoc Phan Cong Day Lop: "+gradeStudentRequest.getClassRoomId()+" Trong Ky "+semesterGroup.getSemesterGroupId(),HttpStatus.BAD_REQUEST);
        }

//        List<ClassRoom> classRoomList = this.classRoomRepository.findByClassRoomId(classRoom1.getClassRoomId());
        //lay sv theo lop do cap nhat diem cho sv trong lop
        for(Class r: classList)
        {
            if(this.classRoomStudentRepository.findByClassRoomAndStudent(r.getId(),student.getPersonId()).isEmpty())
            {
                return new ResponseEntity<>("Ma Sinh Vien "+gradeStudentRequest.getStudentId()+" Khong Thuoc Lop: "+gradeStudentRequest.getClassRoomId(),HttpStatus.BAD_REQUEST);
            }
            Class_Student classRoomStudent = this.classRoomStudentRepository.findByClassRoomAndStudent(r.getId(),student.getPersonId()).get();
            classRoomStudent.setMidScore(gradeStudentRequest.getMidScore());
            classRoomStudent.setEndScore(gradeStudentRequest.getEndScore());
            this.classRoomStudentRepository.save(classRoomStudent);
        }
        this.UpdateScoreForStudent(student,course,semesterGroup,gradeStudentRequest.getClassRoomId());
        return new ResponseEntity<>("Cap Nha Diem thanh cong",HttpStatus.OK);

    }
    public void UpdateScoreForStudent(Student student1, Course course, Semester_Group semesterGroup, String ClassRoomId)
    {
        Student student = this.mapper.map(this.personRepository.findByUserNameOrPersonId(student1.getPersonId()),Student.class);
        float DiemGiuaKy=0f;
        float DiemCuoiKy=0f;
        float DiemTb=student.getScore()*student.getTotalCredits();
        System.out.println("DiemTB:"+DiemTb);
        int n=0;
        System.out.println(student.getPersonId());
        Course_SemesterGroup courseSemesterGroup = this.courseSemesterGroupRepository.findCourseOnSemesterGroup(course.getCourseId(),semesterGroup.getSemesterGroupId());
        System.out.println(courseSemesterGroup.getCourseSemesterGroupId());
        for (Class cl:courseSemesterGroup.getClassList())
        {
            if(cl.getClassRoomId().equals(ClassRoomId))
            {
                for (Class_Student roomStudent: cl.getClassStudents())
                {
                    if(roomStudent.getStudent().equals(student))
                    {

                        n+=1;
                        DiemGiuaKy+=roomStudent.getMidScore();
                        DiemCuoiKy+=roomStudent.getEndScore();
                    }
                }
            }
        }

//        boolean CheckCourse= false;
        CourseGrade co = new CourseGrade();
        //Check List Student Grade  were learn course
        for(CourseGrade c: student.getCourseGradeList())
        {
            if(c.getCourseID().equals(course.getCourseId()))
            {
                System.out.println(c.getCourseID()+"//"+course.getCourseId());
                if(c.getFinalScore()>=4) {
                    System.out.println("hi2");
                    DiemTb -= (c.getFinalScore() * c.getCredits());
                    System.out.println(DiemTb);
                    student.setTotalCredits(student.getTotalCredits() - c.getCredits());
                    System.out.println(student.getTotalCredits());
                    if (student.getTotalCredits() == 0) {
                        System.out.println("toltle :" + 0);
                        student.setScore(0);
                    } else {
                        System.out.println("toltle :" + student.getTotalCredits());
                        student.setScore(DiemTb / student.getTotalCredits());
                        System.out.println(student.getScore());
                    }

                }
                 co = this.courseGardeRepository.findById(c.getId()).get();
            }
        }
        System.out.println(co.getId());
        student.getCourseGradeList().remove(co);
        this.courseGardeRepository.delete(co);
        CourseGrade courseGrade = new CourseGrade();
        System.out.println(course.getCourseId());
        courseGrade.setCourseID(course.getCourseId());
        courseGrade.setStudent(student);
        courseGrade.setCourseName(course.getCourseName());
        courseGrade.setCredits(course.getCredits());

        courseGrade.setMidScore(DiemGiuaKy/n);
        courseGrade.setEndScore(DiemCuoiKy/n);
        courseGrade.setFinalScore(courseGrade.getMidScore()*0.3f+courseGrade.getEndScore()*0.7f);
        if(courseGrade.getMidScore()<4f) {
            courseGrade.setStatus(StatusCourse.HOCLAI);
            courseGrade.setEndScore(0f);
            courseGrade.setFinalScore(0f);
        }
        else
        {
            if(courseGrade.getFinalScore()<4f)
            {
                courseGrade.setStatus(StatusCourse.THILAI);
            }
            else {
                courseGrade.setStatus(StatusCourse.DAT);
//                StatisticsStudent statisticsStudent = this.statisticsStudentRepository.findByCourseId(course.getCourseId());
//                statisticsStudent.setNumberOfStudent(statisticsStudent.getNumberOfStudent() - 1);
//                this.statisticsStudentRepository.save(statisticsStudent);
                student.setTotalCredits(student.getTotalCredits() + course.getCredits());
                System.out.println("Tong Tc:"+student.getTotalCredits());
                DiemTb += (courseGrade.getFinalScore() * courseGrade.getCredits());
                student.setScore((DiemTb / student.getTotalCredits()));
                System.out.println("Diem Tb:"+student.getScore());
            }
        }
        System.out.println(DiemTb);
//        student.setScore(DiemTb/student.getTotalCredits());
        courseGrade = this.courseGardeRepository.save(courseGrade);
        System.out.println(courseGrade.getStudent().getPersonId()+"/"+courseGrade.getCourseID());
        student.getCourseGradeList().add(courseGrade);
       student= this.personRepository.save(student);
       System.out.println(student.getPersonId());
    }
    public ResponseEntity<?>getAllSemester(String teacherId)
    {
        Person  person=this.personRepository.findByUserNameOrPersonId(teacherId).get();
        Teacher teacher = this.mapper.map(person,Teacher.class);

        List<SemesterGroupResponse> responses = new ArrayList<>();
        List<Semester_Group> list = this.semesterGroupRepository.findAll();
        for (Semester_Group a:list)
        {
            SemesterGroupResponse response= new SemesterGroupResponse();
            response.setSemesterGroupId(a.getSemesterGroupId());
            for(Class cl:teacher.getListClasses())
            {

                if(cl.getSemesterGroupId().equals(a.getSemesterGroupId()))
                {
                    Lop lop= new Lop();
                    lop.setClassRoomId(cl.getClassRoomId());
                    lop.setStart(cl.getStart());
                    lop.setFinish(cl.getFinish());
                    lop.setSemesterGroup(cl.getSemesterGroupId());
                    lop.setTeacherId(cl.getTeacher().getPersonId());
                    lop.setClassRoomName(cl.getName());
                    response.getListClass().add(lop);
                }
            }
            responses.add(response);
        }
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }
}
