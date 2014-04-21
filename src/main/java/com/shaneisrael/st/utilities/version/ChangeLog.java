package com.shaneisrael.st.utilities.version;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ChangeLog
{
    private static final String ADDED_PREFIX = "+ ";
    private static final String REMOVED_PREFIX = "- ";
    private static final String CHANGED_PREFIX = "* ";
    private static final String FIXED_PREFIX = "# ";

    @SerializedName("added")
    private List<String> added;

    @SerializedName("removed")
    private List<String> removed;

    @SerializedName("changed")
    private List<String> changed;

    @SerializedName("fixed")
    private List<String> fixed;

    /**
     * 
     * @return list of features added to this version
     */
    public List<String> getAdded()
    {
        return added;
    }

    /**
     * 
     * @return list of removed features from this version
     */
    public List<String> getRemoved()
    {
        return removed;
    }

    /**
     * 
     * @return list of existing features that now have different behavior
     */
    public List<String> getChanged()
    {
        return changed;
    }

    /**
     * 
     * @return list of bugs that have been fixed
     */
    public List<String> getFixed()
    {
        return fixed;
    }

    public boolean hasAdditions()
    {
        return getAdded().size() > 0;
    }

    public boolean hasRemovals()
    {
        return getAdded().size() > 0;
    }

    public boolean hasChanges()
    {
        return getAdded().size() > 0;
    }

    public boolean hasFixes()
    {
        return getAdded().size() > 0;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        if (hasAdditions())
        {
            builder.append("Added:" + System.lineSeparator());
            for (String addition : getAdded())
            {
                builder.append(ADDED_PREFIX + addition + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        if (hasRemovals())
        {
            builder.append("Removed:" + System.lineSeparator());
            for (String removal : getRemoved())
            {
                builder.append(REMOVED_PREFIX + removal + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        if (hasChanges())
        {
            builder.append("Changed:" + System.lineSeparator());
            for (String change : getChanged())
            {
                builder.append(CHANGED_PREFIX + change + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        if (hasFixes())
        {
            builder.append("Bug Fixes:" + System.lineSeparator());
            for (String bugFix : getFixed())
            {
                builder.append(FIXED_PREFIX + bugFix + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}