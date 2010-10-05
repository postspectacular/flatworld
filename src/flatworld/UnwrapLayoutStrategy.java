package flatworld;

import java.util.List;

public interface UnwrapLayoutStrategy {

    public boolean placeFace(UnwrappedFace face, List<UnwrapSheet> sheets);
}
