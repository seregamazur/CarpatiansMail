package employee;

import java.io.Serializable;

public class Employee implements Serializable {
		private String name;
		private String position;
		private String eMail;
		private int level;
		
		
		public Employee(String name, String position, String eMail, int level){
			this.name = name;
			this.position = position;
			this.eMail = eMail;
			this.level = level;
		}
		
		public String getName() {
			return name;
		}
		
		public String getPosition() {
			return position;
		}
		
		public String getEmail() {
			return eMail;
		}
		
		public int getLevel() {
			return level;
		}
}
