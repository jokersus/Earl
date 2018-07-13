package Listeners;

import java.io.Serializable;

public class JoinLeave implements Serializable {
	public String joinImage;
	public String leaveImage;
	public String joinText;
	public String leaveText;
	public String channelID;
	public boolean isImageEnabled;
	public boolean isEnabled = false;

	public JoinLeave() {
		isEnabled = false;
	}

	public JoinLeave(String join, String leave, boolean isEnabled) {
		this.joinImage = join;
		this.leaveImage = leave;
		this.isEnabled = isEnabled;
	}

	public String getJoinImage() {
		return joinImage;
	}

	public void setJoinImage(String joinImage) {
		this.joinImage = joinImage;
	}

	public String getLeaveImage() {
		return leaveImage;
	}

	public void setLeaveImage(String leaveImage) {
		this.leaveImage = leaveImage;
	}

	public String getJoinText() {
		return joinText;
	}

	public void setJoinText(String joinText) {
		this.joinText = joinText;
	}

	public String getLeaveText() {
		return leaveText;
	}

	public void setLeaveText(String leaveText) {
		this.leaveText = leaveText;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}
}
