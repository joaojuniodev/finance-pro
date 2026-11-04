INSERT INTO user_role (
    user_id,
    role_id
)
SELECT
    app_user.id,
    role.id
FROM users AS app_user
JOIN roles AS role
    ON role.name = CASE app_user.username
        WHEN 'demo' THEN 'PRO'
        WHEN 'joaojunio' THEN 'FRE'
        WHEN 'mediano' THEN 'FRE'
        WHEN 'ruim' THEN 'FRE'
    END
WHERE app_user.username IN ('demo', 'joaojunio', 'mediano', 'ruim');
