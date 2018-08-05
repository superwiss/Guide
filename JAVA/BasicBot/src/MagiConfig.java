// 각종 설정
public class MagiConfig {
    private final boolean releaseMode = true;

    // BuildOrderManager에서 건물을 지을 때, 어떤 유닛이 어느 곳에 건설하는지 가이드라인을 표시한다.
    private boolean drawbuildLine = true;

    public MagiConfig() {
	if (true == releaseMode) {
	    drawbuildLine = false;
	}
    }

    public boolean isReleaseMode() {
	return releaseMode;
    }

    public boolean isDrawbuildLine() {
	return drawbuildLine;
    }

    public void setDrawbuildLine(boolean drawbuildLine) {
	this.drawbuildLine = drawbuildLine;
    }
}