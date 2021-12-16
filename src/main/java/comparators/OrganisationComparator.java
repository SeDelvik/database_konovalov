package comparators;

import mainClasses.Organisation;

import java.util.Comparator;

public class OrganisationComparator  implements Comparator<Organisation> {

    @Override
    public int compare(Organisation o1, Organisation o2) {
        return (int)(o1.getId()-o2.getId());
    }
}
