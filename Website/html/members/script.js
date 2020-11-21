const template_role = `<div class="userRole">
                                            <h3 style="color: %role_color%;">%role_name%</h3>
                                            
                                            %users_replace_me%
                                            
                                        </div>`;

const template_user = `
                                                <div class="userWrapper">
                                                    <div class="userAvatar">
                                                        <img src="%avatar%">
                                                    </div>
                                                    <div class="userInfo">
                                                        <div class="displayName" style="color: %role_color%;">
                                                            %display_name%
                                                        </div>
                                                        <p class="userDescrim">
                                                            %actual_name%
                                                        </p>
                                                    </div>
                                                </div>
                                            `

//const IP = "https://ess.golde.org/api/members";
const IP = "http://localhost:8888/members";

$.getJSON(IP, function(data) {
	if (data.success) {
		console.log('I got me data!');
		var roles = data.data.roles;
		for (roleInt in roles) {
			var role = roles[roleInt];
            var roleName = role.name;
            var members = role.members;
            
            var memberHtmlBuilt = "";
            
			for(memberInt in members){
                var member = members[memberInt];
                
                var memberHtml = template_user;
                memberHtml = memberHtml.replace("%display_name%", member.displayName);
                memberHtml = memberHtml.replace("%actual_name%", member.actualName);
                memberHtml = memberHtml.replace("%avatar%", member.avatar);
                memberHtml = memberHtml.replace("%role_color%", role.color);
                
                
                memberHtmlBuilt += memberHtml;
            }

            var roleHtml = template_role;
            roleHtml = roleHtml.replace("%role_name%", role.name);
            roleHtml = roleHtml.replace("%users_replace_me%", memberHtmlBuilt);
            roleHtml = roleHtml.replace("%role_color%", role.color);
			
			$('#usermodule').append(roleHtml);
		}
	}
});
