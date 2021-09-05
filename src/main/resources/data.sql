/*Default Roles*/
insert ignore into role (name, type, color)
values ('default', 'ADMIN', '');
insert ignore into role (name, type, color)
values ('default', 'RESELLER', '');
insert ignore into role (name, type, color)
values ('default', 'LINE', '');

/* Default Permissions*/
insert ignore into permission (name, description, user_type) values ('dashboard_page', 'See Dashboard Page information', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('admins_manage', 'Manage Admins', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('resellers_manage', 'Manage Resellers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('roles_manage', 'Manage Roles', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('permissions_manage', 'Manage Roles', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('lines_manage', 'Manage Lines', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_manage', 'Manage Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_manage', 'Manage Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_manage', 'Manage Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('episodes_manage', 'Manage Episodes', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('collections_manage', 'Manage Collections', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('category_manage', 'Manage Category', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('package_manage', 'Manage Package', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('epg_manage', 'Manage EPG', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('logs_manage', 'Manage Logs', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('servers_manage', 'Manage Servers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('setting_manage', 'Manage Setting', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('connection_manage', 'Manage Connections', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('downloadList_manage', 'Manage DownloadLists', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('backup_manage', 'Manage Backups', 'ADMIN');


insert ignore into setting (id, value) value ('backup_interval', '');
