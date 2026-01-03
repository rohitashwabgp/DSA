import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

enum Permission {
    READ, WRITE, DELETE
}

class Resource {
    String name;
}

interface User {
    Set<Permission> getPermission(Resource resource);

    User getDescription();
}

class UserObject implements User {
    final public Map<Resource, List<Permission>> levelPermissions;
    private final String name;
    private final String userName;
    private final long iamId;

    UserObject(Map<Resource, List<Permission>> permissions, String name, String userName, long iamId) {
        this.levelPermissions = permissions;
        this.name = name;
        this.userName = userName;
        this.iamId = iamId;
    }

    @Override
    public Set<Permission> getPermission(Resource resource) {
        return Set.of(Permission.READ);
    }

    @Override
    public User getDescription() {
        return this;
    }
}

interface Group {
    Set<User> getUser();

    Set<Group> getGroups();

    Group add(Group group);

    User add(User user);

    boolean has(User user);

    boolean has(Group group);

    Set<Permission> getPermission(Resource resource);

    boolean addPermission(Permission permission, Resource resource);
}

class GroupObject implements Group {
    private final Set<User> users;
    private final Set<Group> groups;
    final Map<Resource, Permission> levelPermissions;

    GroupObject(Set<User> users, Set<Group> groups, Map<Resource, Permission> levelPermissions) {
        this.users = users;
        this.groups = groups;
        this.levelPermissions = levelPermissions;
    }

    @Override
    public Set<User> getUser() {
        return this.users;
    }

    @Override
    public Set<Group> getGroups() {
        return this.groups;
    }

    @Override
    public Group add(Group group) {
        if (has(group)) {
            return group;
        }
        this.groups.add(group);
        return null;
    }

    @Override
    public User add(User user) {
        if (has(user)) {
            return user;
        }
        this.users.add(user);
        return null;
    }

    @Override
    public boolean has(User user) {
        return users.contains(user);
    }

    @Override
    public boolean has(Group group) {
        return groups.contains(group);
    }

    @Override
    public Set<Permission> getPermission(Resource resource) {
        return Set.of(Permission.READ, Permission.DELETE);
    }

    @Override
    public boolean addPermission(Permission permission, Resource resource) {
        return false;
    }
}

class PermissionManager {
    final List<Group> groupList;

    PermissionManager(List<Group> groupList) {
        this.groupList = groupList;
    }

    boolean hasPermission(User user, Permission permission, Resource resource) {
        if (checkUserPermission(user, permission)) {
            return true;
        }
        Set<Group> visited = new HashSet<>();
        for (Group group : groupList) {
            if (group.has(user)) {
                if (checkGroupPermission(permission, group, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkUserPermission(User user, Permission permission) {
        return user.getPermission(new Resource()).contains(permission);
    }

    private boolean checkGroupPermission(Permission permission, Group group, Set<Group> visited) {
        if (visited.contains(group) || group.getPermission(new Resource()).isEmpty()) return false;
        visited.add(group);
        for (Group groupNext : group.getGroups()) {
            if (groupNext.getPermission(new Resource()).contains(permission)) {
                return true;
            } else {
                checkGroupPermission(permission, groupNext, visited);
            }
        }
        return false;
    }

}


public class ACLApp {
}
