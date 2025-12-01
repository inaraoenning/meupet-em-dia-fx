- **mvn clean compile**
- **mvn javafx:run**

### **Fluxo de teste:**

1. **Login**
   - Email: `admin@clinica.com`
   - Senha: `admin123`

2. **Cadastrar um Dono**
   - Ir em "Cadastro de Donos"
   - Adicionar: João Silva, (11) 98765-4321, joao@email.com

3. **Cadastrar um Pet**
   - Ir em "Cadastro de Pets"
   - Adicionar: Rex, Cachorro, Labrador, dono João

4. **Registrar Consulta**
   - Ir em "Vacinas e Consultas"
   - Selecionar "Consulta"
   - Registrar consulta para Rex

5. **Registrar Vacina**
   - Mesmo menu, selecionar "Vacina"
   - Escolher V8, data hoje, próxima dose calculada automaticamente

6. **Ver Próximas Vacinas**
   - Ir em "Próximas Vacinas"
   - Ver Rex na lista com status colorido

7. **Ver Relatórios**
   - Ir em "Relatórios"
   - Ver gráficos e estatísticas

---

## 🎨 **FUNCIONALIDADES IMPLEMENTADAS**

### **Gerais:**
- ✅ Autenticação com hash SHA-256
- ✅ Banco SQLite local
- ✅ Navegação fluida entre telas
- ✅ Paleta de cores personalizada
- ✅ Validações em tempo real
- ✅ Máscaras de entrada (telefone)
- ✅ Busca e filtros

### CRUD:
- ✅ Donos (criar, editar, excluir, listar)
- ✅ Pets (criar, editar, excluir, listar)
- ✅ Consultas (criar, excluir, listar)
- ✅ Vacinas (criar, excluir, listar)

### **Relatórios:**
- ✅ Dashboard com cards estatísticos
- ✅ Gráfico de pizza (status vacinas)
- ✅ Gráfico de barras (consultas/mês)
- ✅ Sistema de alertas (vacinas vencidas)

---

## 🚀 **MELHORIAS FUTURAS (OPCIONAL)**

Se quiser expandir o sistema:

1. **Backup automático** do banco de dados
2. **Exportar relatórios** para PDF
3. **Enviar notificações** de vacinas vencidas por e-mail
4. **Histórico médico completo** do pet
5. **Sistema de agendamento** de consultas
6. **Controle de estoque** de medicamentos
7. **Múltiplos usuários** com permissões
8. **Impressão de carteirinhas** de vacinação

---

## 📝 **CREDENCIAIS PADRÃO**
```

E-mail: admin@clinica.com
Senha: admin123