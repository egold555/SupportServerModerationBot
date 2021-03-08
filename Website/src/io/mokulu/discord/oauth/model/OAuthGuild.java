package io.mokulu.discord.oauth.model;

import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
public class OAuthGuild
{
    private String id;
    private String name;
    private String icon;
    private boolean owner;
    private Integer permissions;
    private List<String> features;

    public List<OAuthPermission> getPermissionList()
    {
        List<OAuthPermission> permissionList = new LinkedList<>();
        for (OAuthPermission permission : OAuthPermission.values())
        {
            if (permission.isIn(permissions))
            {
                permissionList.add(permission);
            }
        }
        return permissionList;
    }
}
