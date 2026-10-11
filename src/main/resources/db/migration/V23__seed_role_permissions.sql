INSERT INTO role_permission (
    role_id,
    permission_id
)
SELECT
    role.id,
    permission.id
FROM roles AS role
CROSS JOIN permissions AS permission
WHERE role.name = 'ADMIN'
   OR (
       role.name = 'PRO'
       AND permission.name IN ('FINANCE_READ', 'FINANCE_WRITE', 'REPORTS_READ')
   )
   OR (
       role.name = 'FRE'
       AND permission.name IN ('FINANCE_READ', 'FINANCE_WRITE')
   );
