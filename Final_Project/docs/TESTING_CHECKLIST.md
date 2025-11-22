# Phase 4 Testing - Quick Checklist

**Quick reference for testing Phase 4 Backend Controllers**

---

## Pre-Testing Setup

- [ ] Java installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] PostgreSQL running (`pg_isready`)
- [ ] Database `ai_chat` created
- [ ] Schema applied (`schema.sql`)
- [ ] `application.properties` configured
- [ ] `GEMINI_API_KEY` set
- [ ] Backend server running (`mvn spring-boot:run`)
- [ ] Postman collection imported
- [ ] Postman environment imported and selected

---

## Quick Test Commands

### Authentication
```bash
# Signup
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"password123"}'
# Expected: 201 Created

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser1","password":"password123"}'
# Expected: 200 OK, save userId
```

### Conversations
```bash
# Create (replace {userId})
curl -X POST http://localhost:8080/api/v1/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"title":"My Chat"}'
# Expected: 201 Created, save conversationId

# List
curl -X GET http://localhost:8080/api/v1/conversations \
  -H "X-User-Id: {userId}"
# Expected: 200 OK, array of conversations
```

### Messages
```bash
# Send (replace {userId} and {conversationId})
curl -X POST http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "Content-Type: application/json" \
  -H "X-User-Id: {userId}" \
  -d '{"text":"Hello"}'
# Expected: 200 OK, assistantMessage in response

# Get
curl -X GET http://localhost:8080/api/v1/conversations/{conversationId}/messages \
  -H "X-User-Id: {userId}"
# Expected: 200 OK, array of messages
```

---

## Postman Collection - Quick Run

1. [ ] Open Postman
2. [ ] Select "AI Chat Backend API Environment"
3. [ ] Run collection "AI Chat Backend API - Phase 4"
4. [ ] Verify all tests pass (33+ tests)

**Test Groups:**
- [ ] Authentication (8 tests)
- [ ] Conversations (11 tests)
- [ ] Messages (9 tests)
- [ ] Error Scenarios (5 tests)

---

## Critical Tests

### Must Pass
- [ ] Signup creates user (201)
- [ ] Login authenticates (200)
- [ ] Create conversation (201)
- [ ] Send message gets AI reply (200)
- [ ] Invalid input returns 400
- [ ] Missing header returns 400/401
- [ ] Unauthorized access returns 403
- [ ] Non-existent resource returns 404

### Error Handling
- [ ] Validation errors return 400 with `VALIDATION_ERROR`
- [ ] Authentication errors return 401 with `INVALID_CREDENTIALS`
- [ ] Not found returns 404 with appropriate error code
- [ ] Server errors return 500 without exposing details

---

## Verification Points

- [ ] All HTTP status codes match LLD
- [ ] All error responses match LLD format
- [ ] Response times < 5 seconds
- [ ] No stack traces in error responses
- [ ] Environment variables set correctly in Postman
- [ ] Database persists data correctly
- [ ] Soft delete works (conversations)

---

## Common Issues Quick Fix

| Issue | Quick Fix |
|-------|-----------|
| Port 8080 in use | Change port in `application.properties` |
| Database connection failed | Check PostgreSQL running, verify credentials |
| 500 errors | Check `GEMINI_API_KEY`, check logs |
| Postman vars not set | Run signup/login tests first |
| Validation not working | Check `@Valid` annotations |

---

## Test Results Summary

**Date:** _______________  
**Tester:** _______________

- Total Tests: 33+
- Passed: _______
- Failed: _______
- Pass Rate: _______%

**Status:** [ ] PASS [ ] FAIL [ ] NEEDS REWORK

**Notes:**
_________________________________________________
_________________________________________________
_________________________________________________

---

**For detailed testing procedures, see `TESTING_GUIDE.md`**

