public class Room {

    private float size;
    private float height;

    public Room() {
        // for clear collection
    }

    public Room(float size, float height) {
        this.size = size;
        this.height = height;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
