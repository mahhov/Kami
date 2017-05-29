package editor;

import list.LList;

class SelectButtonGroup {
	private int count;
	private LList<ScreenSelectButton> selectButton;
	
	SelectButtonGroup() {
		selectButton = new LList<>();
	}
	
	ScreenSelectButton add(ScreenSelectButton sb) {
		selectButton = selectButton.add(sb);
		sb.setGroup(count, this);
		if (count++ == 0)
			sb.toggle = true;
		return sb;
	}
	
	void clearSelection() {
		for (LList<ScreenSelectButton> sb : selectButton)
			sb.node.toggle = false;
	}
}
