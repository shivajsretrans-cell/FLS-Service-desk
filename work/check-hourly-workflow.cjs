const base='http://127.0.0.1:8790';
async function call(path,body,cookie=''){const r=await fetch(base+path,{method:body?'POST':'GET',headers:{...(body?{'content-type':'application/json'}:{}),...(cookie?{cookie}:{})},body:body?JSON.stringify(body):undefined});const t=await r.text();return {status:r.status,body:t?JSON.parse(t):{},cookie:r.headers.get('set-cookie')?.match(/fls_sid=([^;]+)/)?.[1]};}
(async()=>{
let s=await call('/api/auth/login',{email:'supervisor@test.invalid',password:'TestSupervisorPass123'});if(s.status!==200)throw Error('supervisor login '+JSON.stringify(s));const sc='fls_sid='+s.cookie;
let l=await call('/api/auth/login',{email:'assistant@test.invalid',password:'TestAssistantPass123'});if(l.status!==200)throw Error('assistant login '+JSON.stringify(l));const ac='fls_sid='+l.cookie;
let c=await call('/api/requests',{customer:'Test',vehicle:'Test Van',reg:'TEST 123',issue:'Hourly update test',status:'Vehicle assigned',assignedAssistant:'Assistant Test',priority:'Normal'},sc);if(c.status!==201)throw Error('create request '+JSON.stringify(c));const ticket=c.body.r[0];
let list=await call('/api/assistant/requests',null,ac);if(list.status!==200||!list.body.requests.some(x=>x.id===ticket.id))throw Error('assignment missing '+JSON.stringify(list));
let progress=await call('/api/assistant/update/'+ticket.id,{progress:'Inspection completed',status:'In progress'},ac);if(progress.status!==200||!progress.body.request.lastAssistantUpdateAt)throw Error('progress failed '+JSON.stringify(progress));
let completed=await call('/api/update/'+ticket.id,{status:'Completed',notes:'Ready'},sc);if(completed.status!==200||!completed.body.r.find(x=>x.id===ticket.id).completedAt)throw Error('completion timestamp missing');
console.log(JSON.stringify({ticket:ticket.id,assistantCanSeeAssignment:true,progressSaved:progress.body.request.lastAssistantUpdateAt,status:progress.body.request.status,completionSaved:completed.body.r.find(x=>x.id===ticket.id).completedAt},null,2));
})().catch(e=>{console.error(e);process.exit(1)});
