package editor;

class ScreenSelectButton extends ScreenToggleButton {
	private int id;
	private SelectButtonGroup group;
	
	ScreenSelectButton(String text) {
		super(text);
	}
	
	void setGroup(int id, SelectButtonGroup group) {
		this.id = id;
		this.group = group;
	}
	
	public boolean handleMouseInput(double screenX, double screenY, int mouseState) {
		super.handleMouseInput(screenX, screenY, mouseState);
		if (press) {
			group.clearSelection();
			toggle = true;
		}
		return !highlight;
	}
}
