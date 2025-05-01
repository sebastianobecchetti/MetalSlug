package it.unipg.entity;
public class Animation {
	private int spriteCounter = 0;
	private int spriteNum = 0;
	private int updateSpeed;

	public Animation(int updateSpeed) {
		this.updateSpeed = updateSpeed;
	}

	public void update(int totalFrames) {
		spriteCounter++;
		if (spriteCounter > updateSpeed) {
			spriteNum = (spriteNum + 1) % totalFrames;
			spriteCounter = 0;
		}
	}

	public int getFrame() {
		return spriteNum;
	}

	public void reset() {
		spriteNum = 0;
		spriteCounter = 0;
	}
}
