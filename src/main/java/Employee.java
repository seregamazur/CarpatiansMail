package employee;

public class Employee {
    private String name;
    private int id;
    private String position;
    private int levelHierarchy;
    private String email;

    public Employee(String name, int id, String position, int levelHierarchy, String email) {
        this.name = name;
        this.id = id;
        this.position = position;
        this.levelHierarchy = levelHierarchy;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public int getLevelHierarchy() {
        return levelHierarchy;
    }

    public String getEmail() {
        return email;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setLevelHierarchy(int levelHierarchy) {
        this.levelHierarchy = levelHierarchy;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
