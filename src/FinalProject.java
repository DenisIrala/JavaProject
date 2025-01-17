import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;

/*
- COP 3330 Final Project.
- Denis Adrian Irala Morales
*/


public class FinalProject {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Person> list= new ArrayList<Person>();
		Scanner Input= new Scanner(System.in);
		String StringInput;
		int IntegerInput;
		ArrayList<Lecture> existingLectures=new ArrayList<Lecture>();
		Person newPerson;
		System.out.printf("Enter the absolute path of the file: ");
		Scanner database=null;
		boolean anyDelete=false;
		String Path=null;
		
		
		boolean found=false;
		do {
			Path=Input.nextLine();
			try {
				database = new Scanner(new File(Path));
				found=true;
				System.out.println("File Found! Let’s proceed…");
			} catch (FileNotFoundException e) {
				System.out.println("Sorry no such file.");
			}
		}while(found==false);
		
		String tempArray[];
		Lecture newLecture=null;
		while(database.hasNextLine()) {
			String line=database.nextLine();
			
			tempArray=line.split(",");
			if(tempArray.length>2) {
				newLecture= new Lecture(tempArray[2]);
				newLecture.id=tempArray[1];
				newLecture.crn=Integer.parseInt(tempArray[0]);
				newLecture.isOnline=(tempArray.length==5);
				existingLectures.add(newLecture);
				//System.out.println(newLecture.crn);
			}
			else {
				
				existingLectures.get(existingLectures.size()-1).possibleLabs.add(tempArray[0]+","+tempArray[1]);
				//existingLectures.get(existingLectures.size()-1).labs.add(tempArray[0]+"/"+tempArray[1]);
				existingLectures.get(existingLectures.size()-1).hasLabs=true;
			}
			
		}
		database.close();
		
		
		
		do {
			
		try {
			
			System.out.println("\n*****************************************");
			System.out.println("Choose one of these options:");
			System.out.println("\t1- Add a new Faculty to the schedule");
			System.out.println("\t2- Enroll a Student to a Lecture");
			System.out.println("\t3- Print the schedule of a Faculty");
			System.out.println("\t4- Print the schedule of an TA"); 	
			System.out.println("\t5- Print the schedule of a Student");
			System.out.println("\t6- Delete a Lecture");
			System.out.println("\t7- Exit");
			System.out.printf("Enter your choice: ");
			int option=Input.nextInt();
			
			switch(option) {
			case 1:
				//1- Add a new faculty to the schedule.
				//(This requires assigning TAs to the labs when applicable)
				//ADD TA COMPATIBILITY
				int id;
				System.out.printf("Enter UCF id:");
				id=Input.nextInt();
				
				if(id<1000000 || id>9999999) throw new IdException();
				
				boolean facultyFound=false;
				newPerson=null;
				Faculty newFaculty=null;
				int listNumber=0;
				
				for(Person p: list) {
					if(id==p.UCFID){
						facultyFound=true;
						newPerson=p;
					}
					listNumber++;
				}
				if(newPerson!=null && newPerson.role!="Faculty") {
					System.out.println(newPerson.role);
					System.out.println("This UCFID already corresponds to a non-faculty person.");
					
				}
				
				if(facultyFound==false) {
					newFaculty=new Faculty();
					newFaculty.UCFID=id;
					Input.nextLine();
					System.out.printf("Enter name:");
					StringInput=Input.nextLine();
					newFaculty.name=StringInput;
					do {
					System.out.printf("Enter rank:");
					StringInput=Input.nextLine();
					if(StringInput.equalsIgnoreCase("Professor") || StringInput.equalsIgnoreCase("Associate Professor") || StringInput.equalsIgnoreCase("Assistant Professor") || StringInput.equalsIgnoreCase("Adjunct"))
						break;
					System.out.println("Please enter a valid rank");
					}while(true);
						
					newFaculty.rank=StringInput;
					System.out.printf("Enter office location:");
					StringInput=Input.nextLine();
					newFaculty.officeLocation=StringInput;
				}
				else {
					newFaculty=(Faculty)newPerson;
				}
					System.out.printf("Enter how many lectures:");
					IntegerInput=Input.nextInt();
					
					System.out.printf("Enter the crns of the lectures:");
					int NumberOfNewLectures=IntegerInput; //For some reason the for loop doesn't work properly without this line.
					
					
					int oldLectureNumber=newFaculty.lectures.size();
					
					
					for(int i=0; i<NumberOfNewLectures; i++) {
						IntegerInput=Input.nextInt();
						
						
						found=false;
						for(Lecture l :existingLectures) {
							if(l.crn==IntegerInput) {
								
								for(Person p: list) { //Checking the lecture hasn't been asigned to anyone else yet.
									if(p.role.equalsIgnoreCase("Faculty")) {
										Faculty examinedFaculty=(Faculty)p;
										for(Lecture l2: examinedFaculty.lectures) {
											if(l2.crn==l.crn) throw new LectureAlreadyAssigned();
										}
									}
								}
								
								newFaculty.lectures.add(l);
								found=true;
								break;
							}
							
							
						
						}
						if(found==false) {
							System.out.println("There does not exist a lecture with that crn.");
							break;
						}
							
					}						
					
					
					//Adding TAs
					
						for(int lectureNumber=oldLectureNumber; lectureNumber<NumberOfNewLectures+oldLectureNumber; lectureNumber++) {
								
									Lecture currentLecture=newFaculty.lectures.get(lectureNumber);
									
									int LabNumber=currentLecture.possibleLabs.size();
									if(!currentLecture.hasLabs) {
										System.out.println("["+currentLecture.crn+"/"+currentLecture.id+"/"+currentLecture.name+"] Added!");
									}
									else{
										System.out.println("["+currentLecture.crn+"/"+currentLecture.id+"/"+currentLecture.name+"] has these labs:");
										for(String lab: currentLecture.possibleLabs) {
											System.out.println("\t"+lab);
										}
									for(int LabCounter=0; LabCounter<LabNumber; LabCounter++) { //Looking for TA
										do { //Checking it isn't a student that is taking the same lecture
										System.out.printf("\nEnter the TA’s id for "+currentLecture.possibleLabs.get(LabCounter).substring(0,5)+":");
										found=false;
										Person searchedPerson;
										boolean repeatedLecture=false;

										IntegerInput=Input.nextInt();

										
										for(Person p: list) { //Check if the UCFID individual is taking the course.
											if(id==IntegerInput){
												found=true;
												searchedPerson=p;
												Student searchedStudent= (Student)searchedPerson;
												
												for(Lecture lecture: searchedStudent.lecturesAndLabs) {
													if(lecture.id==currentLecture.id) {
														System.out.println("A student can’t be a TA for a lecture in which that student is taking.");
														repeatedLecture=true;
													}
												}
											}
										}
										if(!repeatedLecture)break;
										}while(true);
										
										found=false;
										int searchCounter=0;
										TA currentTA=null;
										for(Person t: list) { //Existing TA?
											if(t.role.equalsIgnoreCase("TA") && t.UCFID==IntegerInput) {
												found=true;
												currentTA=(TA) t;
												currentTA.labsSupervised.add(currentLecture.possibleLabs.get(LabCounter));
												list.set(searchCounter, currentTA);
												System.out.println("Lab assigned to "+t.name+"!");
												break;
											}
											searchCounter++;

										}
										if(found==false) { //Existing Student?
											searchCounter=0;
											for(Person p: list) {
												if(p.UCFID==IntegerInput) {
													found=true;
													Student FutureTA=(Student) p;
													TA newTA= new TA(FutureTA);
													System.out.println("TA found as a student: "+newTA.name);
													System.out.printf("TA's supervisor's name: ");
													Input.nextLine();
													newTA.supervisor=Input.nextLine(); 
													System.out.printf("Degree Seeking: ");
													newTA.type=Input.nextLine();
													
													newTA.labsSupervised.add(currentLecture.possibleLabs.get(LabCounter));
													System.out.println("Lab assigned to "+newTA.name+"!");

													//System.out.println("Lab assigned to "+newTA.name+"!");
													list.set(searchCounter, newTA); //Old student replaced with TA.
													break;
												}
												searchCounter++;
											}
											if(found==false) { // No Student OR TA with matching UCFID. Therefore, we need data for new Person.
												Student newStudent=new Student();
												
												newStudent.UCFID=IntegerInput;
												System.out.printf("Name of the TA: ");
												Input.nextLine();
												newStudent.name=Input.nextLine();
												
												TA newTA=new TA(newStudent);
												System.out.println("TA's supervisor's name: ");
												
												newTA.supervisor=Input.nextLine();
												System.out.println("Degree Seeking: ");
												newTA.type=Input.nextLine(); //POSIBLE CAMBIOOOOOOO
			
												newTA.labsSupervised.add(currentLecture.possibleLabs.get(LabCounter));
												
												list.add(newTA);
												//System.out.println("New TA just added!");
												//System.out.println("Lab assigned to "+newTA.name+"!");
											}
										}
										
									}
									System.out.println("["+currentLecture.crn+"/"+currentLecture.id+"/"+currentLecture.name+"] Added!");
									
									}
															
							
					
					
				}
					if(facultyFound==true) list.set(listNumber, newFaculty);
					else list.add(newFaculty);
					
					
					break;		
				
			case 2:
				System.out.printf("Enter UCF id:");
				id=Input.nextInt();
				if(id<1000000 || id>9999999) throw new IdException();
				newPerson=null;
				Lecture currentLecture=null;
				
				int searchCounter=0;
				found=false;
				for(Person p: list) {
					if(id==p.UCFID){
						found=true;
						newPerson=p;
					}
					searchCounter++;
				}				
				
				
				if(found==false) {
					newPerson=new Student();
					System.out.printf("Enter name:");
					Input.nextLine();
					StringInput=Input.nextLine();
					newPerson.name=StringInput;
					do {
						System.out.printf("Enter student type (Undergraduate or Graduate):");
						StringInput=Input.nextLine();
						if(StringInput.equalsIgnoreCase("undergraduate") || StringInput.equalsIgnoreCase("graduate")) {
							break;
						}
						else {
							System.out.println("Please enter a valid input (Undergraduate or Graduate).");
						}
					}while(true);
					newPerson.role=StringInput;
					newPerson.UCFID=id;

				}
				else{
					System.out.println("Enter name: "+newPerson.name);

				}
				
				System.out.printf("Which lecture to enroll ["+newPerson.name+"] in? ");
				IntegerInput=Input.nextInt();
				found=false;
				if(IntegerInput==0) {
					list.add((Student)newPerson);
					System.out.println("New Student added!");
					break;
				}
				
				try {
				for(Lecture l: existingLectures) {
					if(l.crn==IntegerInput){
						found=true;
						currentLecture=l;
						break;
					}
					
				}if(found==false) throw new PersonNotFoundException("Lecture");
				}catch(PersonNotFoundException e) {
					System.out.println(e);
					break;
				}
				if(newPerson.role=="TA") {
					boolean TACriteria=false;
					TA currentTA=(TA)newPerson;
					for(String labs: currentLecture.possibleLabs) {
					for(String l: currentTA.labsSupervised) {
						if(labs.equalsIgnoreCase(l)) {
							System.out.println("A TA cannot be enrolled to a lecture they are supervising.");
							TACriteria=true;
							break;
						}
					}
				}
					if(TACriteria) break;

				}
				int crn=currentLecture.crn;
				Student currentStudent=(Student)(newPerson);
				String Prefix= currentLecture.id.substring(0,2);
				//Same prefix check
				for(Lecture lecture: currentStudent.lecturesAndLabs) {
					if(lecture.id.substring(0,2).equalsIgnoreCase(Prefix)) {
						System.out.println("A student cannot have two lectures with the same prefix");
						throw new PersonNotFoundException("available lecture");
					}
				}
				
				//Searching if the lecture exists in the database
				found=false;
				for(Lecture lecture: existingLectures) {
					if(lecture.crn==IntegerInput) {
						found = true;
						if(lecture.hasLabs) {
							System.out.println("["+currentLecture.crn+"/"+currentLecture.id+"/"+currentLecture.name+"] has these labs:");
							for(String lab: currentLecture.possibleLabs) {
								System.out.println("\t"+lab);
							}
							System.out.println("\n");
							int LabNumber=(new Random()).nextInt(lecture.possibleLabs.size());
							lecture.assignedLab=lecture.possibleLabs.get(LabNumber);
						System.out.println("["+currentStudent.name+"] is added to lab : "+lecture.assignedLab.substring(0,5));
						System.out.println("Student enrolled!");
						}
						currentStudent.lecturesAndLabs.add(lecture);
						if(searchCounter!=list.size()) list.set(searchCounter, currentStudent);
						else list.add(currentStudent);
						break;
					}
				}
				if(found==false) {
					System.out.println("The lecture does not exist within the database.");
				}
				
				break;
				
			case 3:
				System.out.printf("Enter the UCF id: ");
				id=Input.nextInt();
				if(id<1000000 || id>9999999) throw new IdException();
				found=false;
				newPerson=null;				
				try {
				for(Person p: list) {
					if(id==p.UCFID){
						found=true;
						newPerson=p;
					}
					
				}if(found==false) throw new PersonNotFoundException("Faculty");
				}catch(PersonNotFoundException e) {
					System.out.println(e);
					break;
				}
				
				Faculty currentFaculty=(Faculty)newPerson;
				System.out.println(currentFaculty.name+" is teaching the following lectures:");
				for(Lecture l: currentFaculty.lectures) {
					if(l.hasLabs) {
						System.out.println("\t["+l.crn+"/"+l.id+"/"+l.name+"] with Labs:");
						for(String lab: l.possibleLabs) {
							System.out.println("\t\t["+lab+"]");
						};
					}
					else if(l.isOnline) {
						System.out.println("\t["+l.id+"/"+l.name+"][Online]");
						
					}
					else {
						System.out.println("\t["+l.crn+"/"+l.id+"/"+l.name+"]");
					}
				}
				break;
			case 4:
				System.out.printf("Enter the TA's UCF id:");
				id=Input.nextInt();
				if(id<1000000 || id>9999999) throw new IdException();
				newPerson=null;	
				found=false;
				for(Person p: list) {
					if(id==p.UCFID){
						found=true;
						newPerson=p;
					}
					
				}
				if(found==false){
					System.out.println("Sorry no TA found.");
					break;
				}
				else if(newPerson.role!="TA") {
					System.out.println("Sorry no TA found.");
					break;
				}
				TA currentTA=(TA)newPerson;
				//Print schedule
				System.out.println(currentTA.name+" is supervising the following labs:");
				for(String s:currentTA.labsSupervised) {
					System.out.println("\t["+s+"]");
				}
				break;
			case 5:
				System.out.printf("Enter the Student's UCF id:");
				id=Input.nextInt();
				if(id<1000000 || id>9999999) throw new IdException();
				newPerson=null;	
				found=false;
				try {
				for(Person p: list) {
					if(id==p.UCFID){
						found=true;
						newPerson=p;
					}
					
				}if(found==false || newPerson.role=="Faculty") throw new PersonNotFoundException("Student");}
				catch(PersonNotFoundException e) {
					System.out.println(e);
					break;
				}
				currentStudent=(Student)newPerson;
				//Print schedule
				System.out.println("Record Found:");
				System.out.println("\t"+currentStudent.name);
				System.out.println("\tEnrolled in the following lectures");
				for(Lecture l: currentStudent.lecturesAndLabs) {
					System.out.printf("\t["+l.id+"/"+l.name+"]");
					if(l.hasLabs) {
						System.out.println("/[Lab: "+l.assignedLab.substring(0,5)+"]");
					}
				}
				
				break;
			case 6:
				System.out.printf("Enter the crn of the lecture to delete: ");
				crn=Input.nextInt();
				found=false;
				currentLecture=null;
				int i=0;
				for(Lecture l: existingLectures) {
					if(crn==l.crn){
						found=true;
						currentLecture=l;
						break;
					}
					i++;
				}
				if(found==false) {
					System.out.println("The lecture does not exist within the database.");
					break;
				}
				existingLectures.remove(i);
				int peopleSearch=0;
				for(Person p: list) {
					//Delete of all schedules
					if(p.role.equalsIgnoreCase("Student")) {
						currentStudent=((Student) p);
						searchCounter=0;
						for(Lecture l: currentStudent.lecturesAndLabs) {
							if(currentLecture.id==l.id) {
								currentStudent.lecturesAndLabs.remove(searchCounter);
								list.set(peopleSearch, currentStudent);
								break;
							}
							searchCounter++;
						}
					}
					if (p.role.equalsIgnoreCase("TA")) {
						currentTA=((TA) p);
						
						for(String labToEliminate: currentLecture.possibleLabs) {
							
						for(int labCounter=0; labCounter<currentTA.labsSupervised.size(); labCounter++) {
							String l=currentTA.labsSupervised.get(labCounter);
							if(l.equalsIgnoreCase(labToEliminate)) {
								currentTA.labsSupervised.remove(labCounter);
								list.set(peopleSearch, currentTA);
								break;
							}
							
						}
						}
						
						if(currentTA.labsSupervised.size()==0) { //TA is fired and turned back to Student if no more labs
							Student newStudent=new Student(currentTA);
							list.set(peopleSearch, newStudent);
							//System.out.println("TA FIRED");
						}
					}
					if(p.role.equalsIgnoreCase("Faculty")) {
						currentFaculty=(Faculty)p;
						searchCounter=0;
						for(Lecture l2: currentFaculty.lectures) {
							if(l2.id==currentLecture.id) {
								currentFaculty.lectures.remove(searchCounter);
								list.set(peopleSearch, currentFaculty);
								break;
							}
							searchCounter++;
						}
					}
					peopleSearch++;
				}
				
				//Delete from file
				
				File newFile=new File("new.txt");
				File oldFile=new File(Path);
				Scanner oldFileScanner=new Scanner(oldFile);
				//ASK!!!!
				FileWriter fw=new FileWriter("new.txt", true);
				BufferedWriter bw=new BufferedWriter(fw);
				PrintWriter pw= new PrintWriter(bw);
				boolean removeLabs=false;
				
				while(oldFileScanner.hasNextLine()) {
					String line=oldFileScanner.nextLine();
					String arr[]=line.split(",");
					if(arr.length>2) removeLabs=false;
					
					if(removeLabs==true || line.substring(0,5).equalsIgnoreCase(currentLecture.crn+"")) {
						//System.out.println("Deleted");
						removeLabs=true;
						
					}
					else pw.println(line);
					
				}
				oldFileScanner.close();
				pw.flush();
				pw.close();
				fw.close();
				bw.close();
				/*
			    FileWriter f2 = new FileWriter(new File(Path), false);
				Scanner Copy=new Scanner(new File("new.txt"));
				while(Copy.hasNextLine()) {
					f2.write(Copy.nextLine()+"\n");
				}
				f2.close();
				Copy.close();
				*/
				oldFile.delete();
				File Renaming=new File(Path);
				newFile.renameTo(Renaming);

				
				
				
				System.out.println("["+currentLecture.crn+"/"+currentLecture.id+"/"+currentLecture.name+"] Deleted");
				anyDelete=true;
				break;
			case 7:
				if(anyDelete) {
					System.out.printf("You have made a deletion of at least one lecture. Would you like to print the copy of lec.txt? Enter y/Y for Yes or n/N for No: ");
					do {
					StringInput=Input.nextLine();
					if(StringInput.equalsIgnoreCase("Y")) {
						Scanner Print = new Scanner(new File(Path));
						while(Print.hasNextLine()){
							System.out.println(Print.nextLine());
						}
						Print.close();
						break;
					}
					else if(StringInput.equalsIgnoreCase("N")) {
						break;
					}
					else {
						System.out.printf("Is that a yes or no? Enter y/Y for Yes or n/N for No: ");
					}
					}while(true);
				}
				System.out.println("Bye!");
				return;
			default:{
				System.out.println("Enter a valid input.");
				continue;
			}
			}
		}
		catch(IdException e) {
			System.out.println(e.getMessage());
			Input.nextLine();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Enter a valid input.");
			Input.nextLine();
		}
		
	}while(true);

}
}

class IdException extends Exception{
	@Override
	public String getMessage() {
		return ">>>>>>>>>>>Sorry incorrect format. (Ids are 7 digits)";
	}
}

class PersonNotFoundException extends Exception{
	String role;
	@Override
	public String getMessage() {
		return "No "+role+" with this id";
	}
	public PersonNotFoundException(String role) {
		this.role=role;
	}
}

class LectureAlreadyAssigned extends Exception{
	@Override
	public String getMessage() {
		return "A Faculty has already been assigned to the lecture with this crn.";
	}

}

abstract class Person{
	public String name;
	public int UCFID;
	public String role;
	
}

class Lecture{
	public String name;
	public String location;
	public String id;
	public int crn;
	public boolean hasLabs;
	public ArrayList<String> possibleLabs;
	public String assignedLab;
	public boolean isOnline;
	public Lecture(String name) {
		this.name=name;
		hasLabs=false;
		possibleLabs= new ArrayList<String>();
	}
	

}

class Faculty extends Person{
	public String rank;
	public ArrayList<Lecture> lectures;
	public String officeLocation;
	public Faculty() {
		lectures= new ArrayList<Lecture>();
		role="Faculty";
	}
}

class Student extends Person{
	public String type;
	public ArrayList<Lecture> lecturesAndLabs;
	
	public Student() {
		lecturesAndLabs=new ArrayList<Lecture>();
		role="Student";
	}
	public Student(TA s) {
		this.name=s.name;
		this.UCFID=s.UCFID;
		this.type=s.type; 
		this.lecturesAndLabs=s.lecturesAndLabs;
		role="Student";
	}
	
}

class TA extends Student{
	public ArrayList<String> labsSupervised;
	public String supervisor;
	public TA(Student s) {
		labsSupervised=new ArrayList<String>();
		this.name=s.name;
		this.UCFID=s.UCFID;
		this.type=s.type;
		this.lecturesAndLabs=s.lecturesAndLabs;
		role="TA";
	}
}








