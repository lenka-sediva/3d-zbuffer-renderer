package model.objectdata;

public class Part {
    private final int startIndex;
    private final int count;
    private final TopologyType topologyType;

    public Part(int startIndex, int count, TopologyType topologyType) {
        this.startIndex = startIndex;
        this.count = count;
        this.topologyType = topologyType;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getCount() {
        return count;
    }

    public TopologyType getTopologyType() {
        return topologyType;
    }
}

