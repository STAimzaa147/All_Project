package game;

public interface IMovable {
	void updateDirection(char direction);

	void updateVelocity();

	void reset();
}
