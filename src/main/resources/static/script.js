document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('grievanceForm');
    const refreshBtn = document.getElementById('refreshBtn');
    const submitMessage = document.getElementById('submitMessage');

    // Load initial grievances
    fetchGrievances();

    // Handle form submission
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const categoryId = document.getElementById('categoryId').value;
        const description = document.getElementById('description').value;
        const submitBtn = form.querySelector('button');

        try {
            submitBtn.textContent = 'Submitting...';
            submitBtn.disabled = true;

            const response = await fetch('/api/grievances', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // Basic auth headers could go here if Spring Security isn't disabled
                },
                body: JSON.stringify({
                    categoryId: parseInt(categoryId),
                    description: description
                })
            });

            if (response.ok) {
                showMessage('Grievance submitted successfully!', 'success');
                form.reset();
                fetchGrievances(); // Refresh the list
            } else if (response.status === 401) {
                showMessage('Unauthorized! Please configure Spring Security or provide credentials.', 'error');
            } else {
                showMessage('Failed to submit grievance. Does Category ID exist?', 'error');
            }
        } catch (error) {
            showMessage('Network error. Is the server running?', 'error');
        } finally {
            submitBtn.textContent = 'Submit Grievance';
            submitBtn.disabled = false;
        }
    });

    // Handle refresh button
    refreshBtn.addEventListener('click', () => {
        refreshBtn.textContent = 'Refreshing...';
        fetchGrievances().then(() => {
            setTimeout(() => { refreshBtn.textContent = 'Refresh'; }, 500);
        });
    });

    // Fetch and display grievances
    async function fetchGrievances() {
        const grid = document.getElementById('grievanceList');
        
        try {
            // Fetching page 0, size 20
            const response = await fetch('/api/grievances?page=0&size=20&sortBy=createdAt');
            
            if (response.status === 401) {
                grid.innerHTML = '<p style="color:var(--danger)">Access Denied (401). Spring Security is active. You must log in or disable security to view data.</p>';
                return;
            }

            if (!response.ok) throw new Error('Failed to fetch');

            const data = await response.json();
            
            // The API returns a Spring 'Page' object, so data is in data.content
            const grievances = data.content || [];

            if (grievances.length === 0) {
                grid.innerHTML = '<p style="color:var(--text-muted)">No grievances found. Submit one above!</p>';
                return;
            }

            grid.innerHTML = ''; // Clear loading/old items
            
            grievances.forEach(g => {
                const card = document.createElement('div');
                card.className = 'grievance-card';
                
                const date = new Date(g.createdAt).toLocaleDateString(undefined, { 
                    year: 'numeric', month: 'short', day: 'numeric', 
                    hour: '2-digit', minute:'2-digit' 
                });

                // Handle possible null category
                const categoryName = g.category ? g.category.name : 'Category ' + (g.category?.id || 'N/A');

                card.innerHTML = `
                    <div class="card-header">
                        <span class="category">#${g.id} • ${categoryName}</span>
                        <span class="badge status-${g.status}">${g.status}</span>
                    </div>
                    <div class="card-body">
                        <p>${g.description}</p>
                    </div>
                    <div class="card-footer">
                        Submitted: ${date}
                    </div>
                `;
                grid.appendChild(card);
            });
        } catch (error) {
            grid.innerHTML = '<p style="color:var(--danger)">Error loading data. Make sure the backend is running.</p>';
        }
    }

    function showMessage(msg, type) {
        submitMessage.textContent = msg;
        submitMessage.className = `message ${type}`;
        setTimeout(() => {
            submitMessage.className = 'hidden';
        }, 5000);
    }
});
