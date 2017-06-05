package editor;

class ScreenSelectButton extends ScreenToggleButton {
	private int id;
	private SelectButtonGroup group;
	
	ScreenSelectButton(String text) {
		super(text);
	}
	
	ScreenSelectButton(String text, char shortcutKey) {
		super(text, shortcutKey);
	}
	
	void setGroup(int id, SelectButtonGroup group) {
		this.id = id;
		this.group = group;
	}
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		super.handleMouseInput(screenX, screenY, mouseState, charInput, charState);
		if (press)
			group.setSelect(id);
		return !highlight;
	}
}
